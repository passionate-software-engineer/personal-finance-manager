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
public class TransactionQueryResult implements Diffable<TransactionQueryResult> {

  private String date;

  private String description;

  private String price;

  private String account;

  private String category;

  @Override
  public DiffResult diff(TransactionQueryResult obj) {
    return new DiffBuilder(this, obj, ToStringStyle.SHORT_PREFIX_STYLE)
        .append("date", this.date, obj.date)
        .append("description", this.description, obj.description)
        .append("price", this.price, obj.price)
        .append("account", this.account, obj.account)
        .append("category", this.category, obj.category)
        .build();
  }
}
