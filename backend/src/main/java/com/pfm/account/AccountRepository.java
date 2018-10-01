package com.pfm.account;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

  List<Account> findByNameIgnoreCase(String name);

  List<Account> findByUserId(long userId);

  Optional<Account> findByIdAndUserId(long id, long userId);
}