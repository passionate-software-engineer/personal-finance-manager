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
public class AccountQueryResult implements Diffable<AccountQueryResult> {

  private String name;

  private String balance;

  private String currency;

  private String lastVerificationDate;

  private boolean archived;

  @Override
  public DiffResult diff(AccountQueryResult obj) {
    return new DiffBuilder(this, obj, ToStringStyle.SHORT_PREFIX_STYLE)
        .append("name", this.name, obj.name)
        .append("balance", this.balance, obj.balance)
        .append("currency", this.currency, obj.currency)
        .append("lastVerificationDate", this.lastVerificationDate, obj.lastVerificationDate)
        .append("archived", this.archived, obj.archived)
        .build();
  }
}
