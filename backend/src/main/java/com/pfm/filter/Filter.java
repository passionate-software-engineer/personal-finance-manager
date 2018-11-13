package com.pfm.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pfm.history.HistoryField;
import com.pfm.history.HistoryField.IdField;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public final class Filter {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ApiModelProperty(value = "Price from", example = "Food filter")
  @HistoryField(nullAllowed = true)
  private String name;

  @ElementCollection
  @Column(name = "account_id")
  @HistoryField(idFieldName = IdField.AccountIds)
  // add descriptions and example for swagger, up to date(19.10.2018) there is no feature like this
  private List<Long> accountIds;

  @ElementCollection
  @Column(name = "category_id")
  @HistoryField(idFieldName = IdField.CategoryIds)
  // add descriptions and example for swagger, up to date(19.10.2018) there is no feature like this
  private List<Long> categoryIds;

  @ApiModelProperty(value = "Price from", example = "10")
  @HistoryField(nullAllowed = true)
  private BigDecimal priceFrom;

  @ApiModelProperty(value = "Price to", example = "30")
  @HistoryField(nullAllowed = true)
  private BigDecimal priceTo;

  @ApiModelProperty(value = "Date", example = "2018-06-15")
  @HistoryField(nullAllowed = true)
  private LocalDate dateFrom;

  @ApiModelProperty(value = "Date", example = "2018-07-16")
  @HistoryField(nullAllowed = true)
  private LocalDate dateTo;

  @ApiModelProperty(value = "Description", example = "Food expenses")
  @HistoryField(nullAllowed = true)
  private String description;

  @JsonIgnore
  private Long userId;

}
