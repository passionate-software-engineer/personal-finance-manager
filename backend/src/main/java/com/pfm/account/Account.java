package com.pfm.account;

import static com.pfm.helpers.BigDecimalHelper.convertBigDecimalToString;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pfm.history.DifferenceProvider;
import com.pfm.history.HistoryEntryProvider;
import com.pfm.history.HistoryField;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public final class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @ApiModelProperty(value = "Account id (generated by application)", required = true, example = "1")
  public Long id;

  @ApiModelProperty(value = "Account name", required = true, example = "Alior Bank savings account")
  @HistoryField
  public String name;

  @ApiModelProperty(value = "Account's balance", required = true, example = "1438.89")
  @HistoryField
  public BigDecimal balance;

  @JsonIgnore
  public Long userId;

//  @Override
//  public List<String> getDifferences(Account otherAccount) {
//    List<String> differences = new ArrayList<>();
//
//    if (!(this.getName().equals(otherAccount.getName()))) {
//      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "name", this.getName(), otherAccount.getName()));
//    }
//
//    if (!(this.getBalance().compareTo(otherAccount.getBalance()) == 0)) {
//      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "balance", this.getBalance().toString(),
//          convertBigDecimalToString(otherAccount.getBalance())));
//    }
//
//    return differences;
//  }
//
//  @Override
//  public List<String> getObjectPropertiesWithValues() {
//    List<String> newValues = new ArrayList<>();
//    newValues.add(String.format(ENTRY_VALUES_TEMPLATE, "name", this.getName()));
//    newValues.add(String.format(ENTRY_VALUES_TEMPLATE, "balance", convertBigDecimalToString(this.getBalance())));
//    return newValues;
//  }
//
//  @Override
//  public String getObjectDescriptiveName() {
//    return this.getName();
//  }
}