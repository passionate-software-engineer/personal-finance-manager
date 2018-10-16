package com.pfm.auth;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<AppUser, Long> {

  Optional<AppUser> findByUsernameIgnoreCase(String username);

  Optional<AppUser> findByUsername(String username);

  AppUser findByUsernameAndPassword(String username, String password);
}
