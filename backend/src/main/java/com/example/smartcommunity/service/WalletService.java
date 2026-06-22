package com.example.smartcommunity.service;

import com.example.smartcommunity.entity.Wallet;

import java.math.BigDecimal;

public interface WalletService {

    /**
     * 获取或创建用户钱包
     */
    Wallet getWallet(Long userId);

    /**
     * 充值
     * @return 更新后的钱包
     */
    Wallet recharge(Long userId, BigDecimal amount);

    /**
     * 扣款（无订单关联）
     * @return true-成功, false-余额不足
     */
    boolean pay(Long userId, BigDecimal amount);

    /**
     * 扣款（关联订单号，写入流水时可追溯）
     * @param userId  用户ID
     * @param amount  金额
     * @param orderNo 关联订单号
     * @return true-成功, false-余额不足
     */
    boolean pay(Long userId, BigDecimal amount, String orderNo);

    /**
     * 查询余额
     */
    BigDecimal getBalance(Long userId);
}
