package com.pfm.helpers;

import static com.pfm.export.ImportService.CATEGORY_NAMED_IMPORTED;

import com.pfm.category.Category;

public class TestCategoryProvider {

  public static Category categoryImported() {
    return Category.builder()
        .id(1L)
        .name(CATEGORY_NAMED_IMPORTED)
        .build();
  }

  public static Category categoryCar() {
    return Category.builder()
        .id(1L)
        .name("Car")
        .priority(500)
        .build();
  }

  public static Category categoryFood() {
    return Category.builder()
        .name("Food")
        .priority(3)
        .build();
  }

  public static Category categoryOil() {
    return Category.builder()
        .id(2L)
        .name("Oil")
        .priority(9)
        .build();
  }

  public static Category categoryGearBoxOil() {
    return Category.builder()
        .id(5L)
        .name("Gear box oil")
        .priority(12)
        .build();
  }

  public static Category categoryHome() {
    return Category.builder()
        .id(2L)
        .priority(2)
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


