package com.pfm.account.type;

import com.pfm.account.Account;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountTypeRepository extends CrudRepository<AccountType, Long> {

  List<AccountType> findByUserId(long userId);

  Optional<AccountType> findByIdAndUserId(long id, long userId);

  @Query("select account type from AccountType accountType where lower(accountType.name) like lower(:nameToFind) AND accountType.userId = :id")
  List<AccountType> findByNameIgnoreCaseAndUserId(@Param("nameToFind") String nameToFind, @Param("id") long id);

}
