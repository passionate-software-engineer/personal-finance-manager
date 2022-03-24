package com.pfm.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pfm.history.HistoryField;
import com.pfm.history.HistoryField.SpecialFieldType;
import io.swagger.v3.oas.annotations.media.Schema;
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
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public final class Filter {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Schema(description = "Price from", example = "Food filter")
  @HistoryField
  private String name;

  @LazyCollection(LazyCollectionOption.FALSE)
  @ElementCollection
  @Column(name = "account_id")
  @HistoryField(fieldType = SpecialFieldType.ACCOUNT_IDS)
  @Schema(description = "Account ids", example = "[1, 7, 19]")
  private List<Long> accountIds;

  @LazyCollection(LazyCollectionOption.FALSE)
  @ElementCollection
  @Column(name = "category_id")
  @HistoryField(fieldType = SpecialFieldType.CATEGORY_IDS)
  @Schema(description = "Category ids", example = "[1, 3, 99]")
  private List<Long> categoryIds;

  @Schema(description = "Price from", example = "10")
  @HistoryField(nullable = true)
  private BigDecimal priceFrom;

  @Schema(description = "Price to", example = "30")
  @HistoryField(nullable = true)
  private BigDecimal priceTo;

  @Schema(description = "Date", example = "2018-06-15")
  @HistoryField(nullable = true)
  private LocalDate dateFrom;

  @Schema(description = "Date", example = "2018-07-16")
  @HistoryField(nullable = true)
  private LocalDate dateTo;

  @Schema(description = "Description", example = "Food expenses")
  @HistoryField(nullable = true)
  private String description;

  @JsonIgnore
  private Long userId;

  private Boolean isDefault;

}
