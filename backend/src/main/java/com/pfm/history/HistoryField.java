package com.pfm.history;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HistoryField {

  SpecialFieldType idFieldName() default SpecialFieldType.NONE;

  boolean nullable() default false;

  enum SpecialFieldType {
    CATEGORY, NONE, PARENT_CATEGORY, ACCOUNT_PRICE_ENTRY, CATEGORY_IDS, ACCOUNT_IDS;
  }

}
