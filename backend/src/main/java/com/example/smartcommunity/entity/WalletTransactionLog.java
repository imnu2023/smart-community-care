package com.example.smartcommunity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("wallet_transaction_log")
public class WalletTransactionLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    /**
     * 变动类型: 1-充值, 2-消费, 3-退款
     */
    @TableField("transaction_type")
    private Integer transactionType;

    /**
     * 变动金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 变动后的余额快照
     */
    @TableField("balance_after")
    private BigDecimal balanceAfter;

    /**
     * 关联的服务订单号
     */
    @TableField("related_order_no")
    private String relatedOrderNo;

    @TableField("remark")
    private String remark;

    @TableField("created_at")
    private LocalDateTime createdAt;
}