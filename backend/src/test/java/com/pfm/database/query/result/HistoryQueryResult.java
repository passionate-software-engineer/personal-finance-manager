package com.pfm.database.query.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoryQueryResult {

  private String date;

  private String type;

  private String object;

  private String name;

  private String oldValue;

  private String newValue;

}
