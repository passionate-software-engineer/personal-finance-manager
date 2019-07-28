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
public class CurrencyQueryResult implements Diffable<CurrencyQueryResult> {

  private String name;

  private String exchangeRate;

  @Override
  public DiffResult diff(CurrencyQueryResult obj) {
    return new DiffBuilder(this, obj, ToStringStyle.SHORT_PREFIX_STYLE)
        .append("name", this.name, obj.name)
        .append("exchangeRate", this.exchangeRate, obj.exchangeRate)
        .build();
  }
}
