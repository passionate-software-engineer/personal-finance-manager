package com.pfm.history;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HistoryField {

  IdFieldName idFieldName() default IdFieldName.None;

  enum IdFieldName {
    Category, Account, None, ParentCategory, AccountPriceEntry
  }

}
