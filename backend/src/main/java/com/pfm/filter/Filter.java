package com.pfm.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
//TODO add descriptions and exaples for swagger
public final class Filter {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @ElementCollection
  @Column(name = "account_id")
  private List<Long> accountIds;

  @ElementCollection
  @Column(name = "category_id")
  private List<Long> categoryIds;

  private BigDecimal priceFrom;

  private BigDecimal priceTo;

  private LocalDate dateFrom;

  private LocalDate dateTo;

  private String description;

  private long userId;

}
