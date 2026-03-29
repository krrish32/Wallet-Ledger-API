package com.wallet.ledger.dto;
import com.wallet.ledger.entity.WalletStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletResponse {
    private Long id;
    private String ownerName;
    private String phoneNumber;
    private BigDecimal balance;
    private WalletStatus status;
    private LocalDateTime createdAt;

}
