package com.pfm.account.type;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountTypeRepository extends CrudRepository<AccountType, Long> {

  List<AccountType> findByUserId(long userId);

  Optional<AccountType> findByIdAndUserId(long id, long userId);

}
