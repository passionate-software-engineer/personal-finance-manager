package com.pfm.history;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HistoryField {

  IdField idFieldName() default IdField.None;

  boolean nullAllowed() default false;

  enum IdField {
    Category, Account, None, ParentCategory, AccountPriceEntry, CategoryIds, AccountIds
  }

}
