package com.pfm.auth;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRespository extends CrudRepository<User, Long> {

  @Query(value = "select count(user.username) from User user where user.username = :usernameToCheck")
  Integer numberOfUsersWithThisUsername(@Param("usernameToCheck") String usernameToCheck);

  User findByUsernameAndPassword(String username, String password);
}
