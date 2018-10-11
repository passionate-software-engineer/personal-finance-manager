package com.pfm.helpers;

import com.pfm.auth.Userek;

public class TestUsersProvider {

  public static Userek userMarian() {
    return Userek.builder()
        .username("Marian")
        .password("Marian")
        .firstName("Marian")
        .lastName("Pazdioch")
        .build();
  }

  public static Userek userZdzislaw() {
    return Userek.builder()
        .firstName("Zdzislaw")
        .lastName("Krecina")
        .username("Zdzislaw")
        .password("Zdzislaw")
        .build();
  }

}
