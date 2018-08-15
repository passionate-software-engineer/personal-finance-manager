package com.pfm.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pfm.account.Account;
import com.pfm.category.Category;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
public final class Filter {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @OneToMany
  private List<Account> accounts;

  @OneToMany
  private List<Category> categories;

  private BigDecimal priceFrom;
  private BigDecimal priceTo;

  private LocalDate dateFrom;
  private LocalDate dateTo;

  private String description;
}
