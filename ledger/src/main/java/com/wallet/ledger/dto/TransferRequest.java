package com.wallet.ledger.dto;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequest {
    private Long fromWallet;
    private Long toWallet;
    private String description;
    private BigDecimal amount;
}
