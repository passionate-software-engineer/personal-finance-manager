package com.pfm.history;

import com.pfm.account.AccountService;
import com.pfm.category.CategoryService;
import com.pfm.history.HistoryField.idFieldName;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HistoryEntryProvider {

  private AccountService accountService;
  private CategoryService categoryService;

  public List<HistoryInfo> createHistoryEntryOnAdd(Object newObject) {

    List<HistoryInfo> historyInfos = new ArrayList<>();
    Field[] fields = newObject.getClass().getDeclaredFields();

    for (Field field : fields) {
      field.setAccessible(true);
      if (field.isAnnotationPresent(HistoryField.class)) {
        String value = getValueFromField(field);

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
      //old object
      Object valueFromOldObject = null;
      valueFromOldObject = getObject(fieldsFromOldObject[i], valueFromOldObject);

      //new object
      Object valueFromNewObject = null;
      valueFromNewObject = getObject(fieldsFromNewObject[i], valueFromNewObject);

      HistoryInfo historyInfo = HistoryInfo.builder()
          .name(fieldsFromOldObject[i].getName())
          .oldValue(valueFromOldObject.toString())
          .newValue(valueFromNewObject.toString())
          .build();
      historyInfos.add(historyInfo);
    }

    return historyInfos;
  }

  public List<HistoryInfo> createHistoryEntryOnDelete(Object oldObject) {
    List<HistoryInfo> historyInfos = new ArrayList<>();

    Field[] fields = oldObject.getClass().getDeclaredFields();

    for (Field field : fields) {
      Object value = null;

      value = getObject(field, value);

      HistoryInfo historyInfo = HistoryInfo.builder()
          .name(field.getName())
          .oldValue(value.toString())
          .build();
      historyInfos.add(historyInfo);
    }

    return historyInfos;
  }

  private Object getObject(Field field, Object value) {
    try {
      if (Modifier.isPublic(field.getModifiers())) {
        value = field.get(this);
      } else {
        field.setAccessible(true);
        value = field.get(this);
      }
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } finally {
      field.setAccessible(false);
    }
    return value;
  }

  private String getValueFromField(Field field) {
    String value = null;
    if (field.getAnnotation(HistoryField.class).getidFieldName().equals(idFieldName.None)) {
      try {
        value = (String) field.get(new Object());
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    } else if (field.getAnnotation(HistoryField.class).getidFieldName().equals(idFieldName.Category)) {
    }
    return value;
  }
}
