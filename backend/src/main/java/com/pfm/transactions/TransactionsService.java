package com.pfm.transactions;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@AllArgsConstructor
@Service
public class TransactionsService {

    private TransactionsRepository transactionsRepository;

    public Optional<Transactions> getTransactionsById(long id) {

        return transactionsRepository.findById(id);
    }

    public List<Transactions> getTransactions() {
        return StreamSupport.stream(transactionsRepository.findAll().spliterator(), false)
                .sorted(Comparator.comparing(Transactions::getTransaction_id))
                .collect(Collectors.toList());
    }

    public Transactions addTransactions(Transactions transactions) {
        return transactionsRepository.save(transactions);
    }

    public void updateTransactions(long transactions_id, Transactions transactions) {
        Optional<Transactions> transactionsFromDb = getTransactionsById(transactions_id);

        if (!transactionsFromDb.isPresent()) {
            throw new IllegalStateException("Transactions with id: " + transactions_id + " does not exist in database");
        }

        Transactions transactionsToUpdate = transactionsFromDb.get();
        transactionsToUpdate.setTransaction_description(transactions.getTransaction_description());
        transactionsToUpdate.setTransaction_category(transactions.getTransaction_category());
        transactionsToUpdate.setTransaction_account(transactions.getTransaction_account());

        transactionsRepository.save(transactionsToUpdate);
    }

    public void deleteTransctions(long id) {
        transactionsRepository.deleteById(id);
    }

    public boolean idExist(long id) {
        return transactionsRepository.existsById(id);
    }

}
