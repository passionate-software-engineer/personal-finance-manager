package com.pfm.account;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

  List<Account> findByNameContainingIgnoreCase(String name);
}