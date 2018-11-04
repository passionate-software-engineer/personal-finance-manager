package com.pfm.filter;

import static com.pfm.helpers.BigDecimalHelper.convertBigDecimalToString;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pfm.history.DifferenceProvider;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
public final class Filter implements DifferenceProvider<Filter> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ApiModelProperty(value = "Price from", example = "Food filter")
  private String name;

  @ElementCollection
  @Column(name = "account_id")
  // add descriptions and example for swagger, up to date(19.10.2018) there is no feature like this
  private List<Long> accountIds;

  @ElementCollection
  @Column(name = "category_id")
  // add descriptions and example for swagger, up to date(19.10.2018) there is no feature like this
  private List<Long> categoryIds;

  @ApiModelProperty(value = "Price from", example = "10")
  private BigDecimal priceFrom;

  @ApiModelProperty(value = "Price to", example = "30")
  private BigDecimal priceTo;

  @ApiModelProperty(value = "Date", example = "2018-06-15")
  private LocalDate dateFrom;

  @ApiModelProperty(value = "Date", example = "2018-07-16")
  private LocalDate dateTo;

  @ApiModelProperty(value = "Description", example = "Food expenses")
  private String description;

  @JsonIgnore
  private Long userId;

  @Override
  public List<String> getDifferences(Filter filter) {
    List<String> differences = new ArrayList<>();

    //name
    if (!(this.getName().equals(filter.getName()))) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "name", this.getName(), filter.getName()));
    }

    //account ids
    if (!(this.getAccountIds().equals(filter.getAccountIds()))) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "accounts ids", this.getAccountIds().toString(), filter.getAccountIds().toString()));
    }

    //category ids
    if (!(this.getCategoryIds().equals(filter.getCategoryIds()))) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "categories ids", this.getCategoryIds().toString(), filter.getCategoryIds().toString()));
    }

    //price from 
    if (this.getPriceFrom() == null && filter.getPriceFrom() != null) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "price from", "empty", convertBigDecimalToString(filter.getPriceFrom())));
    }

    if (this.getPriceFrom() != null && filter.getPriceFrom() == null) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "price from", convertBigDecimalToString(this.getPriceFrom()), "empty"));
    }

    if (this.getPriceFrom() != null && filter.getPriceFrom() != null && !this.getPriceFrom().equals(filter.getPriceFrom())) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "price from", convertBigDecimalToString(this.getPriceFrom()),
          convertBigDecimalToString(filter.getPriceFrom())));
    }

    //price to
    if (this.getPriceTo() == null && filter.getPriceTo() != null) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "price to", "empty", convertBigDecimalToString(filter.getPriceTo())));
    }

    if (this.getPriceTo() != null && filter.getPriceTo() == null) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "price to", convertBigDecimalToString(this.getPriceTo()), "empty"));
    }

    if (this.getPriceTo() != null && filter.getPriceTo() != null && !this.getPriceTo().equals(filter.getPriceTo())) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "price to", convertBigDecimalToString(this.getPriceTo()),
          convertBigDecimalToString(filter.getPriceTo())));
    }

    //date from
    if (this.getDateFrom() == null && filter.getDateFrom() != null) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "date from", "empty", filter.getDateFrom().toString()));
    }

    if (this.getDateFrom() != null && filter.getDateFrom() == null) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "date from", this.getDateFrom().toString(), "empty"));
    }

    if (this.getDateFrom() != null && filter.getDateFrom() != null && !this.getDateFrom().equals(filter.getDateFrom())) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "date from", this.getDateFrom().toString(), filter.getDateFrom().toString()));
    }

    //date to
    if (this.getDateTo() == null && filter.getDateTo() != null) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "date to", "empty", filter.getDateTo().toString()));
    }

    if (this.getDateTo() != null && filter.getDateTo() == null) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "date to", this.getDateTo().toString(), "empty"));
    }

    if (this.getDateTo() != null && filter.getDateTo() != null && !this.getDateTo().equals(filter.getDateTo())) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "date to", this.getDateTo().toString(), filter.getDateTo().toString()));
    }

    //description
    if (this.getDescription() == null && filter.getDescription() != null) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "description", "empty", filter.getDescription()));
    }

    if (this.getDescription() != null && filter.getDescription() == null) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "description", this.getDescription(), "empty"));
    }

    if (this.getDescription() != null && filter.getDescription() != null && !this.getDescription().equals(filter.getDescription())) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "description", this.getDescription(), filter.getDescription()));
    }

    return differences;
  }

  @Override
  public List<String> getObjectPropertiesWithValues() {
    List<String> newValues = new ArrayList<>();

    newValues.add(String.format(ENTRY_VALUES_TEMPLATE, "name", this.getName()));

    if (!this.getAccountIds().isEmpty()) {
      newValues.add(String.format(ENTRY_VALUES_TEMPLATE, "accounts ids", this.getAccountIds().toString()));
    }

    if (!this.getCategoryIds().isEmpty()) {
      newValues.add(String.format(ENTRY_VALUES_TEMPLATE, "categories ids", this.getCategoryIds().toString()));
    }

    if (this.getPriceFrom() != null) {
      newValues.add(String.format(ENTRY_VALUES_TEMPLATE, "price from", convertBigDecimalToString(this.getPriceFrom())));
    }

    if (this.getPriceTo() != null) {
      newValues.add(String.format(ENTRY_VALUES_TEMPLATE, "price to", convertBigDecimalToString(this.getPriceTo())));
    }

    if (this.getDateFrom() != null) {
      newValues.add(String.format(ENTRY_VALUES_TEMPLATE, "date from", this.getDateFrom().toString()));
    }

    if (this.getDateTo() != null) {
      newValues.add(String.format(ENTRY_VALUES_TEMPLATE, "date to", this.getDateTo().toString()));
    }

    if (this.getDescription() != null) {
      newValues.add(String.format(ENTRY_VALUES_TEMPLATE, "description", this.getDescription()));
    }

    return newValues;
  }

  @Override
  public String getObjectDescriptiveName() {
    return this.getName();
  }

}
