package com.pfm.helpers;

import com.pfm.category.Category;

public class TestCategoryProvider {

  public static Category categoryCar() {
    return Category.builder()
        .id(1L)
        .name("Car")
        .build();
  }

  public static Category categoryFood() {
    return Category.builder()
        .name("Food")
        .build();
  }

  public static Category categoryOil() {
    return Category.builder()
        .id(2L)
        .name("Oil")
        .build();
  }

  public static Category categoryHome() {
    return Category.builder()
        .id(2L)
        .name("Home")
        .build();
  }

  public static Category categoryAnimals() {
    return Category.builder()
        .id(2L)
        .name("Animals")
        .build();
  }
}


