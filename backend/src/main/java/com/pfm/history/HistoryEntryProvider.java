package com.pfm.history;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class HistoryEntryProvider {
  
  public List<HistoryInfo> createHistoryEntryOnAdd() {

    List<HistoryInfo> historyInfos = new ArrayList<>();

    Field[] fields = this.getClass().getDeclaredFields();

    for (Field field : fields) {
      Object value = null;

      value = getObject(field, value);

      HistoryInfo historyInfo = HistoryInfo.builder()
          .name(field.getName())
          .newValue(value.toString())
          .build();
      historyInfos.add(historyInfo);
    }

    return historyInfos;
  }

  public List<HistoryInfo> createHistoryEntryOnUpdate(T t) {

    List<HistoryInfo> historyInfos = new ArrayList<>();

    Field[] fieldsFromOldObject = this.getClass().getDeclaredFields();
    Field[] fieldsFromNewObject = t.getClass().getDeclaredFields();

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

  public List<HistoryInfo> createHistoryEntryOnDelete() {
    List<HistoryInfo> historyInfos = new ArrayList<>();

    Field[] fields = this.getClass().getDeclaredFields();

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
}
