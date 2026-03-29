package com.wallet.ledger.service;

import com.wallet.ledger.dto.AmountRequest;
import com.wallet.ledger.dto.CreateWalletRequest;
import com.wallet.ledger.dto.TransferRequest;
import com.wallet.ledger.dto.WalletResponse;
import com.wallet.ledger.entity.TransactionType;
import com.wallet.ledger.entity.Wallet;
import com.wallet.ledger.entity.WalletStatus;
import com.wallet.ledger.entity.WalletTransaction;
import com.wallet.ledger.exceptions.ResourceNotFoundException;
import com.wallet.ledger.repository.WalletRepository;
import com.wallet.ledger.repository.WalletTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    WalletService( WalletRepository walletRepository, WalletTransactionRepository walletTransactionRepository){
        this.walletRepository = walletRepository;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    public WalletResponse createWallet(CreateWalletRequest request){
        Wallet wallet = new Wallet();
        wallet.setOwnerName(request.getOwnerName());
        wallet.setPhoneNumber(request.getPhoneNumber());
        wallet.setStatus(WalletStatus.ACTIVE);
        wallet.setBalance(BigDecimal.ZERO);
        Wallet saveWallet = walletRepository.save(wallet);
        return mapToResponse(saveWallet);
    }

    public WalletResponse getWalletById(Long id){
        Wallet wallet = getWalletEntity(id);
        return mapToResponse(wallet);
    }

    public BigDecimal getWalletBalance(Long id){
        Wallet wallet = getWalletEntity(id);
        return wallet.getBalance();
    }

    @Transactional
    public WalletResponse credit(Long walletId, AmountRequest amountRequest){
        validAmount(amountRequest.getAmount());

        Wallet wallet = getWalletEntity(walletId);

        wallet.setBalance(wallet.getBalance().add(amountRequest.getAmount()));
        walletRepository.save(wallet);

        createTransaction(wallet,TransactionType.CREDIT, amountRequest.getAmount(), amountRequest.getDescription(), UUID.randomUUID().toString());
        return mapToResponse(wallet);
    }

    @Transactional
    public WalletResponse debit(Long walletId, AmountRequest amountRequest){
        validAmount(amountRequest.getAmount());

        Wallet wallet = getWalletEntity(walletId);

        wallet.setBalance(wallet.getBalance().subtract(amountRequest.getAmount()));
        walletRepository.save(wallet);

        createTransaction(wallet,TransactionType.DEBIT, amountRequest.getAmount(), amountRequest.getDescription(), UUID.randomUUID().toString());
        return mapToResponse(wallet);
    }

    @Transactional
    public String transfer(TransferRequest transferRequest){
        validAmount(transferRequest.getAmount());

        if(transferRequest.getFromWallet().equals(transferRequest.getToWallet())){
            throw new IllegalArgumentException("Sender and receiver wallet cannot be same");
        }

        Wallet sender = getWalletEntity(transferRequest.getFromWallet());
        Wallet receiver = getWalletEntity(transferRequest.getToWallet());

        if(sender.getBalance().compareTo(transferRequest.getAmount())<0){
            throw new IllegalArgumentException("Insufficient balance in sender wallet");
        }

        String referenceId = UUID.randomUUID().toString();

        sender.setBalance(sender.getBalance().subtract(transferRequest.getAmount()));
        receiver.setBalance(receiver.getBalance().add(transferRequest.getAmount()));

        walletRepository.save(sender);
        walletRepository.save(receiver);
        createTransaction(sender,TransactionType.TRANSFER_OUT,transferRequest.getAmount(),transferRequest.getDescription(),referenceId);
        createTransaction(receiver,TransactionType.TRANSFER_IN,transferRequest.getAmount(),transferRequest.getDescription(),referenceId);

        return "Transfer completed successfully with reference id:"+ referenceId;
    }

    public List<WalletTransaction> getTransactions(Long walletId){
        getWalletEntity(walletId);
        return walletTransactionRepository.findByWalletIdOrderByCreatedAt(walletId);
    }


    public void createTransaction(Wallet wallet, TransactionType type,BigDecimal amt, String desc, String referenceId){
        WalletTransaction walletTransaction = new WalletTransaction();
        walletTransaction.setWallet(wallet);
        walletTransaction.setReferenceId(referenceId);
        walletTransaction.setAmount(amt);
        walletTransaction.setDescription(desc);
        walletTransaction.setType(type);

        walletTransactionRepository.save(walletTransaction);
    }

    public Wallet getWalletEntity(Long walletId){
        return walletRepository.findById(walletId).orElseThrow(()->new ResourceNotFoundException("Wallet not found with id:"+ walletId));
    }

    public void validAmount(BigDecimal amt){
        if(amt==null || amt.compareTo(BigDecimal.ZERO)<=0){
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }



    private WalletResponse mapToResponse(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getOwnerName(),
                wallet.getPhoneNumber(),
                wallet.getBalance(),
                wallet.getStatus(),
                wallet.getCreatedAt()
        );
    }
}
