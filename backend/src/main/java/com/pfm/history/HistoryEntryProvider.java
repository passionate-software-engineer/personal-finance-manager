package com.pfm.history;

import com.pfm.account.Account;
import com.pfm.account.AccountService;
import com.pfm.auth.UserProvider;
import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import com.pfm.history.HistoryField.IdFieldName;
import com.pfm.transaction.AccountPriceEntry;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HistoryEntryProvider {

  private AccountService accountService;
  private CategoryService categoryService;
  private UserProvider userProvider;

  public List<HistoryInfo> createHistoryEntryOnAdd(Object newObject) {

    List<HistoryInfo> historyInfos = new ArrayList<>();
    Field[] fields = newObject.getClass().getDeclaredFields();

    for (Field field : fields) {
      field.setAccessible(true);
      if (field.isAnnotationPresent(HistoryField.class)) {
        String value = getValueFromField(field, newObject);
        HistoryInfo historyInfo = HistoryInfo.builder()
            .name(field.getName())
            .newValue(value)
            .build();
        historyInfos.add(historyInfo);
      }
    }

    return historyInfos;
  }

  public List<HistoryInfo> createHistoryEntryOnUpdate(Object oldObject, Object newObject) {

    List<HistoryInfo> historyInfos = new ArrayList<>();

    Field[] fieldsFromOldObject = oldObject.getClass().getDeclaredFields();
    Field[] fieldsFromNewObject = newObject.getClass().getDeclaredFields();

    for (int i = 0; i < fieldsFromNewObject.length; i++) {
      fieldsFromOldObject[i].setAccessible(true);
      fieldsFromNewObject[i].setAccessible(true);
      if (fieldsFromOldObject[i].isAnnotationPresent(HistoryField.class)) {
        String valueFromOldObject = getValueFromField(fieldsFromOldObject[i], oldObject);
        String valueFromNewObject = getValueFromField(fieldsFromNewObject[i], newObject);

        HistoryInfo historyInfo = HistoryInfo.builder()
            .name(fieldsFromOldObject[i].getName())
            .oldValue(valueFromOldObject)
            .newValue(valueFromNewObject)
            .build();
        historyInfos.add(historyInfo);
      }
    }

    return historyInfos;
  }

  public List<HistoryInfo> createHistoryEntryOnDelete(Object oldObject) {
    List<HistoryInfo> historyInfos = new ArrayList<>();

    Field[] fields = oldObject.getClass().getDeclaredFields();

    for (Field field : fields) {
      field.setAccessible(true);
      if (field.isAnnotationPresent(HistoryField.class)) {

        String value = getValueFromField(field, oldObject);

        HistoryInfo historyInfo = HistoryInfo.builder()
            .name(field.getName())
            .oldValue(value)
            .build();
        historyInfos.add(historyInfo);
      }
    }
    return historyInfos;
  }

  @SuppressWarnings("unchecked")
  private String getValueFromField(Field field, Object object) {

    Object value = null;
    final IdFieldName idFieldName = field.getAnnotation(HistoryField.class).idFieldName();

    switch (idFieldName) {

      case ParentCategory: {
        Category category = (Category) getValue(field, object);
        if (category == null) {
          value = "Main Category";
        } else {
          value = category.getName();
        }
        break;
      }

      case Category: {
        value = categoryService.getCategoryFromDbByIdAndUserId((Long) getValue(field, object), userProvider.getCurrentUserId()).getName();
        break;
      }

      case AccountPriceEntry: {
        List<AccountPriceEntry> accountPriceEntries = (List<AccountPriceEntry>) getValue(field, object);
        List<String> values = new ArrayList<>();
        for (AccountPriceEntry accountPriceEntry : accountPriceEntries) {
          Long accountId = accountPriceEntry.getAccountId();
          Account account = accountService.getAccountFromDbByIdAndUserId(accountId, userProvider.getCurrentUserId());
          values.add(String.format("%s - %s", account.getName(), accountPriceEntry.getPrice()));
        }
        value = values;
        break;
      }

      default:
        value = getValue(field, object);
        break;

    }

    if (value == null) {
      throw new IllegalStateException();
    }

    return value.toString();
  }

  private Object getValue(Field field, Object object) {
    Object value = null;
    try {
      value = field.get(object);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return value;
  }
}