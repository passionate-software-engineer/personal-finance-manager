package com.pfm.helpers;

import com.pfm.auth.User;

public class TestUsersProvider {

  public static User userMarian() {
    return User.builder()
        .username("Marian")
        .password("Marian")
        .firstName("Marian")
        .lastName("Pazdioch")
        .build();
  }

}
