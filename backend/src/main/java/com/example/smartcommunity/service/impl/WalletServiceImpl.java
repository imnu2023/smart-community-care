package com.example.smartcommunity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.smartcommunity.entity.Wallet;
import com.example.smartcommunity.entity.WalletTransactionLog;
import com.example.smartcommunity.mapper.WalletMapper;
import com.example.smartcommunity.mapper.WalletTransactionLogMapper;
import com.example.smartcommunity.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletMapper walletMapper;

    private final WalletTransactionLogMapper transactionLogMapper;

    /**
     * 交易类型常量
     */
    private static final int TX_TYPE_RECHARGE = 1;  // 充值
    private static final int TX_TYPE_CONSUME  = 2;  // 消费
    private static final int TX_TYPE_REFUND   = 3;  // 退款

    @Override
    public Wallet getWallet(Long userId) {
        QueryWrapper<Wallet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<Wallet> wallets = walletMapper.selectList(queryWrapper);

        Wallet wallet = null;
        if (wallets != null && !wallets.isEmpty()) {
            wallet = wallets.get(0);
        }

        if (wallet == null) {
            wallet = createWallet(userId);
        }
        if (wallet.getBalance() == null) {
            wallet.setBalance(BigDecimal.ZERO);
        }
        return wallet;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Wallet recharge(Long userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("充值金额必须大于0");
        }

        Wallet wallet = getWallet(userId);
        BigDecimal balanceBefore = wallet.getBalance();
        wallet.setBalance(balanceBefore.add(amount));
        wallet.setUpdatedAt(LocalDateTime.now());

        // 乐观锁更新：检查受影响行数，为0表示版本冲突
        int rows = walletMapper.updateById(wallet);
        if (rows == 0) {
            throw new RuntimeException("钱包并发修改冲突，请重试");
        }

        // 写入交易流水
        writeTransactionLog(userId, TX_TYPE_RECHARGE, amount, wallet.getBalance(),
                null, "账户充值");

        return wallet;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean pay(Long userId, BigDecimal amount) {
        return payInternal(userId, amount, null, "消费支出");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean pay(Long userId, BigDecimal amount, String orderNo) {
        return payInternal(userId, amount, orderNo, "订单支付: " + orderNo);
    }

    /**
     * 内部扣款逻辑：校验余额 → 乐观锁更新 → 写流水
     */
    private boolean payInternal(Long userId, BigDecimal amount, String orderNo, String remark) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("支付金额必须大于0");
        }

        Wallet wallet = getWallet(userId);
        if (wallet.getBalance().compareTo(amount) < 0) {
            return false; // 余额不足
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setUpdatedAt(LocalDateTime.now());

        // 乐观锁更新：检查受影响行数，为0表示版本冲突
        int rows = walletMapper.updateById(wallet);
        if (rows == 0) {
            throw new RuntimeException("钱包并发修改冲突，请重试");
        }

        // 写入交易流水
        writeTransactionLog(userId, TX_TYPE_CONSUME, amount, wallet.getBalance(),
                orderNo, remark);

        return true;
    }

    @Override
    public BigDecimal getBalance(Long userId) {
        Wallet wallet = getWallet(userId);
        return wallet.getBalance();
    }

    /**
     * 创建钱包（带并发安全保护）
     * 如果唯一约束冲突（其他线程已创建），则重新查询返回
     */
    private Wallet createWallet(Long userId) {
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setVersion(0);
        wallet.setCreatedAt(LocalDateTime.now());
        wallet.setUpdatedAt(LocalDateTime.now());

        try {
            walletMapper.insert(wallet);
        } catch (DuplicateKeyException e) {
            // 并发场景下其他线程已创建，重新查询返回
            QueryWrapper<Wallet> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            List<Wallet> wallets = walletMapper.selectList(queryWrapper);
            if (wallets != null && !wallets.isEmpty()) {
                return wallets.get(0);
            }
            throw new RuntimeException("创建钱包失败: " + e.getMessage(), e);
        }

        return wallet;
    }

    /**
     * 写入资金变动流水记录
     */
    private void writeTransactionLog(Long userId, int transactionType, BigDecimal amount,
                                     BigDecimal balanceAfter, String orderNo, String remark) {
        WalletTransactionLog log = new WalletTransactionLog();
        log.setUserId(userId);
        log.setTransactionType(transactionType);
        log.setAmount(amount);
        log.setBalanceAfter(balanceAfter);
        log.setRelatedOrderNo(orderNo);
        log.setRemark(remark);
        log.setCreatedAt(LocalDateTime.now());
        transactionLogMapper.insert(log);
    }
}
