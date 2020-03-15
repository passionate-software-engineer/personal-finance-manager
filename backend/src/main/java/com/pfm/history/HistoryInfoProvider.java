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

  public static final String UNCHECKED = "unchecked";
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
      throw new IllegalStateException(e);
    }
    return value;
  }

  private List<Field> getFieldsDeclaredAsHistoryFields(Field... fields) {
    return Arrays.stream(fields)
        .peek(field -> field.setAccessible(true))
        .filter(field -> field.isAnnotationPresent(HistoryField.class))
        .collect(Collectors.toList());
  }

  private Object getObjectForParentCategoryField(Field field, Object object, @SuppressWarnings("unused") Long userId) {
    Category category = (Category) getValue(field, object);
    return category == null ? getMessage(MessagesProvider.MAIN_CATEGORY) : category.getName();
  }

  private Object getObjectForCategoryField(Field field, Object object, Long userId) {
    final long categoryId = (Long) getValue(field, object);
    return categoryService.getCategoryFromDbByIdAndUserId(categoryId, userId).getName();
  }

  @SuppressWarnings(UNCHECKED)
  private Object getObjectForCategoryIdsField(Field field, Object object, Long userId) {
    List<Long> categoryIds = (List<Long>) getValue(field, object);
    List<String> categoriesName = new ArrayList<>();
    for (long id : categoryIds) {
      String accountName = categoryService.getCategoryFromDbByIdAndUserId(id, userId).getName();
      categoriesName.add(accountName);
    }
    return categoriesName;
  }

  @SuppressWarnings(UNCHECKED)
  private Object getObjectForAccountsIdsField(Field field, Object object, Long userId) {
    List<Long> accountsIds = (List<Long>) getValue(field, object);
    List<String> accountsNames = new ArrayList<>();
    for (long id : accountsIds) {
      String accountName = accountService.getAccountFromDbByIdAndUserId(id, userId).getName();
      accountsNames.add(accountName);
    }
    return accountsNames;
  }

  @SuppressWarnings(UNCHECKED)
  private Object getObjectForAccountPriceEntriesField(Field field, Object object, Long userId) {
    List<AccountPriceEntry> accountPriceEntries = (List<AccountPriceEntry>) getValue(field, object);
    List<String> values = new ArrayList<>();
    for (AccountPriceEntry accountPriceEntry : accountPriceEntries) {
      Long accountId = accountPriceEntry.getAccountId();
      Account account = accountService.getAccountFromDbByIdAndUserId(accountId, userId);
      values.add(String.format("%s : %s", account.getName(), accountPriceEntry.getPrice()));
    }
    return values;
  }

  private Object getObjectForNoneField(Field field, Object object, @SuppressWarnings("unused") Long userId) {
    return getValue(field, object);
  }

  private Map<SpecialFieldType, SpecialFieldFunction> getSpecialFieldsMap() {
    Map<SpecialFieldType, SpecialFieldFunction> map = new HashMap<>();
    map.put(SpecialFieldType.PARENT_CATEGORY, this::getObjectForParentCategoryField);
    map.put(SpecialFieldType.CATEGORY_IDS, this::getObjectForCategoryIdsField);
    map.put(SpecialFieldType.CATEGORY, this::getObjectForCategoryField);
    map.put(SpecialFieldType.ACCOUNT_IDS, this::getObjectForAccountsIdsField);
    map.put(SpecialFieldType.ACCOUNT_PRICE_ENTRY, this::getObjectForAccountPriceEntriesField);
    map.put(SpecialFieldType.NONE, this::getObjectForNoneField);
    return map;
  }

  @FunctionalInterface
  interface SpecialFieldFunction {

    Object apply(Field field, Object object, Long userId);
  }
}
