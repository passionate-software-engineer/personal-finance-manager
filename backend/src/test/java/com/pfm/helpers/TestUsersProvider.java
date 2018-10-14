package com.pfm.helpers;

import com.pfm.auth.AppUser;

public class TestUsersProvider {

  public static AppUser userMarian() {
    return AppUser.builder()
        .username("Marian")
        .password("Marian")
        .firstName("Marian")
        .lastName("Pazdioch")
        .build();
  }

  public static AppUser userZdzislaw() {
    return AppUser.builder()
        .firstName("Zdzislaw")
        .lastName("Krecina")
        .username("Zdzislaw")
        .password("Zdzislaw")
        .build();
  }

}
