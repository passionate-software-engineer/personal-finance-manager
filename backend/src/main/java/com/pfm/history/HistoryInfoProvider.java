package com.pfm.history;

import static com.pfm.config.MessagesProvider.getMessage;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import com.pfm.config.MessagesProvider;
import com.pfm.history.HistoryField.SpecialFieldType;
import com.pfm.transaction.AccountPriceEntry;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HistoryInfoProvider {

  private AccountService accountService;
  private CategoryService categoryService;

  public List<HistoryInfo> createHistoryEntryOnAdd(Object newObject, long userId) {

    List<HistoryInfo> historyInfos = new ArrayList<>();

    final List<Field> fieldsDeclaredAsHistoryFields = getFieldsDeclaredAsHistoryFields(newObject.getClass().getDeclaredFields());

    for (Field field : fieldsDeclaredAsHistoryFields) {

      String value = getValueFromField(field, newObject, userId);
      HistoryInfo historyInfo = HistoryInfo.builder()
          .name(field.getName())
          .newValue(value)
          .build();
      historyInfos.add(historyInfo);
    }

    return historyInfos;
  }

  public <T> List<HistoryInfo> createHistoryEntryOnUpdate(T oldObject, T newObject, long userId) {
    List<HistoryInfo> historyInfos = new ArrayList<>();

    final List<Field> fieldsFromOldObjectDeclaredAsHistoryFields = getFieldsDeclaredAsHistoryFields(oldObject.getClass().getDeclaredFields());
    final List<Field> fieldsFromNewObjectDeclaredAsHistoryFields = getFieldsDeclaredAsHistoryFields(newObject.getClass().getDeclaredFields());

    for (int i = 0; i < fieldsFromOldObjectDeclaredAsHistoryFields.size(); i++) {

      String valueFromOldObject = getValueFromField(fieldsFromOldObjectDeclaredAsHistoryFields.get(i), oldObject, userId);
      String valueFromNewObject = getValueFromField(fieldsFromNewObjectDeclaredAsHistoryFields.get(i), newObject, userId);

      HistoryInfo historyInfo = HistoryInfo.builder()
          .name(fieldsFromOldObjectDeclaredAsHistoryFields.get(i).getName())
          .oldValue(valueFromOldObject)
          .newValue(valueFromNewObject)
          .build();
      historyInfos.add(historyInfo);
    }

    return historyInfos;
  }

  public List<HistoryInfo> createHistoryEntryOnDelete(Object oldObject, long userId) {
    List<HistoryInfo> historyInfos = new ArrayList<>();

    final List<Field> fieldsDeclaredAsHistoryFields = getFieldsDeclaredAsHistoryFields(oldObject.getClass().getDeclaredFields());

    for (Field field : fieldsDeclaredAsHistoryFields) {

      String value = getValueFromField(field, oldObject, userId);

      HistoryInfo historyInfo = HistoryInfo.builder()
          .name(field.getName())
          .oldValue(value)
          .build();
      historyInfos.add(historyInfo);
    }

    return historyInfos;
  }

  @SuppressWarnings("unchecked")
  String getValueFromField(Field field, Object object, long userId) {

    final SpecialFieldType specialFieldType = field.getAnnotation(HistoryField.class).fieldType();

    Object value = getSpecialFieldsMap().get(specialFieldType).apply(field, object, userId);

    if (value == null && !field.getAnnotation(HistoryField.class).nullable()) {
      throw new IllegalStateException("Field value is null");
    }

    return value == null ? null : value.toString();
  }

  Object getValue(Field field, Object object) {
    Object value;
    try {
      value = field.get(object);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    return value;
  }

  private List<Field> getFieldsDeclaredAsHistoryFields(Field[] fields) {
    return Arrays.stream(fields)
        .peek(field -> field.setAccessible(true))
        .filter(field -> field.isAnnotationPresent(HistoryField.class))
        .collect(Collectors.toList());
  }

  private SpecialFieldFunction<Field, Object, Long> getObjectForParentCategoryField() {
    return (field, object, along) -> {
      Category category = (Category) getValue(field, object);
      return category == null ? getMessage(MessagesProvider.MAIN_CATEGORY) : category.getName();
    };
  }

  private SpecialFieldFunction<Field, Object, Long> getObjectForCategoryField() {
    return (field, object, along) -> {
      final long categoryId = (Long) getValue(field, object);
      return categoryService.getCategoryFromDbByIdAndUserId(categoryId, along).getName();
    };
  }

  @SuppressWarnings("unchecked")
  private SpecialFieldFunction<Field, Object, Long> getObjectForNoSpecialField() {
    return (field, object, along) ->
        getValue(field, object);
  }

  @SuppressWarnings("unchecked")
  private SpecialFieldFunction<Field, Object, Long> getObjectForCategoryIdsField() {
    return (field, object, along) -> {
      List<Long> categoryIds = (List<Long>) getValue(field, object);
      List<String> categoriesName = new ArrayList<>();
      for (long id : categoryIds) {
        String accountName = categoryService.getCategoryFromDbByIdAndUserId(id, along).getName();
        categoriesName.add(accountName);
      }
      return categoriesName;
    };
  }

  @SuppressWarnings("unchecked")
  private SpecialFieldFunction<Field, Object, Long> getObjectForAccountsIdsField() {
    return (field, object, along) -> {
      List<Long> accountsIds = (List<Long>) getValue(field, object);
      List<String> accountsNames = new ArrayList<>();
      for (long id : accountsIds) {
        String accountName = accountService.getAccountFromDbByIdAndUserId(id, along).getName();
        accountsNames.add(accountName);
      }
      return accountsNames;
    };
  }

  @SuppressWarnings("unchecked")
  private SpecialFieldFunction<Field, Object, Long> getObjectForAccountPriceEntriesField() {
    return (field, object, along) -> {
      List<AccountPriceEntry> accountPriceEntries = (List<AccountPriceEntry>) getValue(field, object);
      List<String> values = new ArrayList<>();
      for (AccountPriceEntry accountPriceEntry : accountPriceEntries) {
        Long accountId = accountPriceEntry.getAccountId();
        Account account = accountService.getAccountFromDbByIdAndUserId(accountId, along);
        values.add(String.format("%s : %s", account.getName(), accountPriceEntry.getPrice()));
      }
      return values;
    };
  }

  private Map<SpecialFieldType, SpecialFieldFunction<Field, Object, Long>> getSpecialFieldsMap() {
    Map<SpecialFieldType, SpecialFieldFunction<Field, Object, Long>> map = new HashMap<>();
    map.put(SpecialFieldType.PARENT_CATEGORY, getObjectForParentCategoryField());
    map.put(SpecialFieldType.CATEGORY_IDS, getObjectForCategoryIdsField());
    map.put(SpecialFieldType.CATEGORY, getObjectForCategoryField());
    map.put(SpecialFieldType.ACCOUNT_IDS, getObjectForAccountsIdsField());
    map.put(SpecialFieldType.ACCOUNT_PRICE_ENTRY, getObjectForAccountPriceEntriesField());
    map.put(SpecialFieldType.NONE, getObjectForNoSpecialField());

    return map;
  }

  @FunctionalInterface
  interface SpecialFieldFunction<F extends Field, O, L extends Long> {

    Object apply(F field, O object, L along);
  }

}
