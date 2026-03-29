package com.wallet.ledger.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    public  ResourceNotFoundException(String message){
        super(message);
    }
}
