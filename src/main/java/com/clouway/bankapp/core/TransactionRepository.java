package com.clouway.bankapp.core;

import java.util.List;

public interface TransactionRepository {

    void save(Transaction transaction);

    List<Transaction> getUserTransactions(int id, int page, int pageSize);
    List<Transaction> getUserTransactions(int id);
}
