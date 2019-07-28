package com.pfm.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoryQueryResult implements Diffable<HistoryQueryResult> {

  private String date;
  private String type;
  private String object;
  private String name;
  private String oldValue;
  private String newValue;

  @Override
  public DiffResult diff(HistoryQueryResult obj) {
    return new DiffBuilder(this, obj, ToStringStyle.SHORT_PREFIX_STYLE)
        .append("date", this.date, obj.date)
        .append("type", this.type, obj.type)
        .append("object", this.object, obj.object)
        .append("name", this.name, obj.name)
        .append("oldValue", this.oldValue, obj.oldValue)
        .append("newValue", this.newValue, obj.newValue)
        .build();
  }
}
