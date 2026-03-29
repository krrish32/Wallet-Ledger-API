package com.wallet.ledger.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ownerName;
    private String phoneNumber;

    @Column(nullable = false,precision = 19,scale = 2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private WalletStatus status;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist(){
        if(balance==null){
            balance = BigDecimal.ZERO;
        }
        if(createdAt==null){
            createdAt=LocalDateTime.now();
        }
        if(status==null){
            status = WalletStatus.ACTIVE;
        }
    }


}
