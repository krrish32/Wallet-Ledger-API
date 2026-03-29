package com.wallet.ledger.controller;
import com.wallet.ledger.dto.AmountRequest;
import com.wallet.ledger.dto.CreateWalletRequest;
import com.wallet.ledger.dto.TransferRequest;
import com.wallet.ledger.dto.WalletResponse;
import com.wallet.ledger.entity.WalletTransaction;
import com.wallet.ledger.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService){
        this.walletService= walletService;
    }

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@RequestBody CreateWalletRequest createWalletRequest){
        return ResponseEntity.ok(walletService.createWallet(createWalletRequest));
    }

    @GetMapping("/{id}")
    public  ResponseEntity<WalletResponse>getWallet(@PathVariable Long id){
        return  ResponseEntity.ok(walletService.getWalletById(id));
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long id){
        return  ResponseEntity.ok(walletService.getWalletBalance(id));
    }

    @PostMapping("/{id}/credit")
    public ResponseEntity<WalletResponse> credit(@PathVariable Long id, @RequestBody AmountRequest request) {
        return ResponseEntity.ok(walletService.credit(id, request));
    }

    @PostMapping("/{id}/debit")
    public ResponseEntity<WalletResponse> debit(@PathVariable Long id, @RequestBody AmountRequest request) {
        return ResponseEntity.ok(walletService.debit(id, request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request) {
        return ResponseEntity.ok(walletService.transfer(request));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<WalletTransaction>> getTransactions(@PathVariable Long id) {
        return ResponseEntity.ok(walletService.getTransactions(id));
    }
}
