package com.pfm.transaction;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.pfm.config.MessagesProvider.*;

@Component
public class TransactionValidator {

    public List<String> validate(Transaction transaction) {
        List<String> validationErrors = new ArrayList<>();
        if (transaction.getDescription() == null || transaction.getDescription().trim().equals("")) {
            validationErrors.add(getMessage(EMPTY_TRANSACTION_NAME));
        }
        if (transaction.getCategory() == null || transaction.getCategory().trim().equals("")) {
            validationErrors.add(getMessage(EMPTY_TRANSACTION_CATEGORY));
        }
        if(transaction.getAccount()==null || transaction.getAccount().trim().equals("")){
            validationErrors.add(getMessage(EMPTY_TRANSACTION_ACCOUNT_NAME));
        }
        if (transaction.getPrice() == null) {
            validationErrors.add(getMessage(EMPTY_TRANSACTION_PRICE));
        }
        return validationErrors;
    }
}
