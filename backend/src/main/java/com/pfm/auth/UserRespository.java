package com.pfm.auth;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRespository extends CrudRepository<AppUser, Long> {

  @Query(value = "select count(user.username) from AppUser user where user.username = :usernameToCheck")
  Integer numberOfUsersWithThisUsername(@Param("usernameToCheck") String usernameToCheck);

  AppUser findByUsernameAndPassword(String username, String password);
}
