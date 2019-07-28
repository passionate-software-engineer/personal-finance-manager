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
public class FilterQueryResult implements Diffable<FilterQueryResult> {

  private String name;

  private String description;

  private String priceFrom;

  private String priceTo;

  private String dateFrom;

  private String dateTo;

  @Override
  public DiffResult diff(FilterQueryResult obj) {
    return new DiffBuilder(this, obj, ToStringStyle.SHORT_PREFIX_STYLE)
        .append("name", this.name, obj.name)
        .append("description", this.description, obj.description)
        .append("priceFrom", this.priceFrom, obj.priceFrom)
        .append("priceTo", this.priceTo, obj.priceTo)
        .append("dateFrom", this.dateFrom, obj.dateFrom)
        .append("dateTo", this.dateTo, obj.dateTo)
        .build();
  }
}
