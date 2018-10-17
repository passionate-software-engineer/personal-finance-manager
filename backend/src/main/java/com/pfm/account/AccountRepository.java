package com.pfm.account;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

  //TODO add test for scenario where two users add same name for Account
  @Query("select a from Account a where lower(a.name) like lower(concat('%', :nameToFind,'%')) AND a.userId = :id")
  List<Account> findByNameIgnoreCaseAndUserId(@Param("nameToFind") String nameToFind, @Param("id") long id);

  List<Account> findByUserId(long userId);

  Optional<Account> findByIdAndUserId(long id, long userId);
}