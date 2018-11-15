package com.pfm.history;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HistoryField {

  SpecialFieldType fieldType() default SpecialFieldType.NONE;

  boolean nullable() default false;

  enum SpecialFieldType {
    CATEGORY, PARENT_CATEGORY, NONE, ACCOUNT_PRICE_ENTRY, CATEGORY_IDS, ACCOUNT_IDS
  }
}

