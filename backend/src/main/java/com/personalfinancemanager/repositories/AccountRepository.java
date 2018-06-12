package com.personalfinancemanager.repositories;

import com.personalfinancemanager.model.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository  extends CrudRepository<Account, Long> {

}
