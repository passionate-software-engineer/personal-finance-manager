package com.pfm.transaction;

import org.springframework.data.repository.CrudRepository;

public interface AccountPriceEntriesRepository extends CrudRepository<AccountPriceEntry, Long> {

  boolean existsByAccountId(long id);

}
