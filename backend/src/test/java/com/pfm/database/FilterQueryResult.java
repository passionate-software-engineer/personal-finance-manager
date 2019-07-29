package com.pfm.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilterQueryResult {

  private String name;

  private String description;

  private String priceFrom;

  private String priceTo;

  private String dateFrom;

  private String dateTo;

}
