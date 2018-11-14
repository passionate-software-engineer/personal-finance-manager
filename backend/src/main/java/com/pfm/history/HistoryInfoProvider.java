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
import java.util.List;
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

  //ENHANCEMENT use Map<Constant,Handler> instead of this switch or make enum
  String getValueFromField(Field field, Object object, long userId) {

    Object value;
    final SpecialFieldType specialFieldType = field.getAnnotation(HistoryField.class).idFieldName();

    switch (specialFieldType) {

      case PARENT_CATEGORY: {
        value = getObjectForParentCategory(field, object);
        break;
      }

      case CATEGORY: {
        value = categoryService.getCategoryFromDbByIdAndUserId((Long) getValue(field, object), userId).getName();
        break;
      }

      case ACCOUNT_PRICE_ENTRY: {
        value = getObjectForAccountPriceEntries(field, object, userId);
        break;
      }

      case ACCOUNT_IDS: {
        value = getObjectForAccountIds(field, object, userId);
        break;
      }

      case CATEGORY_IDS: {
        value = getObjectForCategoryIds(field, object, userId);
        break;
      }

      default:
        value = getValue(field, object);
        break;

    }

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

  @SuppressWarnings("unchecked")
  private Object getObjectForCategoryIds(Field field, Object object, long userId) {
    List<Long> categoryIds = (List<Long>) getValue(field, object);
    List<String> categoriesName = new ArrayList<>();
    for (long id : categoryIds) {
      String accountName = categoryService.getCategoryFromDbByIdAndUserId(id, userId).getName();
      categoriesName.add(accountName);
    }
    return categoriesName.toString();
  }

  @SuppressWarnings("unchecked")
  private Object getObjectForAccountPriceEntries(Field field, Object object, long userId) {
    List<AccountPriceEntry> accountPriceEntries = (List<AccountPriceEntry>) getValue(field, object);
    List<String> values = new ArrayList<>();
    for (AccountPriceEntry accountPriceEntry : accountPriceEntries) {
      Long accountId = accountPriceEntry.getAccountId();
      Account account = accountService.getAccountFromDbByIdAndUserId(accountId, userId);
      values.add(String.format("%s : %s", account.getName(), accountPriceEntry.getPrice()));
    }
    return values;
  }

  private Object getObjectForParentCategory(Field field, Object object) {
    Category category = (Category) getValue(field, object);
    if (category == null) {
      return getMessage(MessagesProvider.MAIN_CATEGORY);
    }
    return category.getName();
  }

  @SuppressWarnings("unchecked")
  private Object getObjectForAccountIds(Field field, Object object, long userId) {
    List<Long> accountsIds = (List<Long>) getValue(field, object);
    List<String> accounts = new ArrayList<>();
    for (long id : accountsIds) {
      String accountName = accountService.getAccountFromDbByIdAndUserId(id, userId).getName();
      accounts.add(accountName);
    }
    return accounts.toString();
  }

}
