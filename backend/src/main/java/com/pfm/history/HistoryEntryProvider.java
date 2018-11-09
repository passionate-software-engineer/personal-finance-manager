package com.pfm.history;

import com.pfm.account.Account;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class HistoryEntryProvider {

  public static List<HistoryInfo> createHistoryEntryOnAdd(Account account) {

    List<HistoryInfo> historyInfos = new ArrayList<>();

    Field[] fields = account.getClass().getDeclaredFields();

    for (Field field : fields) {
      Object value = null;

      try {
        if (Modifier.isPublic(field.getModifiers())) {
          value = field.get(account);
        } else {
          field.setAccessible(true);
          value = field.get(account);
          field.setAccessible(false);
        }
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }

      HistoryInfo historyInfo = HistoryInfo.builder()
          .name(field.getName())
          .newValue(value.toString())
          .build();
      historyInfos.add(historyInfo);
    }

    return historyInfos;
  }
}
