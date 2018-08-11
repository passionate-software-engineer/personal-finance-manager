package com.pfm.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pfm.account.Account;
import com.pfm.category.Category;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
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
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @ApiModelProperty(value = "Transaction id", required = true, example = "1")
  private Long id;

  @NotNull
  @ApiModelProperty(value = "Description", required = true, example = "Cinema - Star Wars 5")
  private String description;

  @ManyToOne
  @NotNull
  @ApiModelProperty(value = "Category", required = true)
  private Category category;

  @ManyToOne
  @NotNull
  @ApiModelProperty(value = "Account", required = true)
  private Account account;

  @NotNull
  @ApiModelProperty(value = "Price", required = true, example = "15.99")
  private BigDecimal price;

  @NotNull
  @ApiModelProperty(value = "Date", required = true, example = "10-02-2018")
  private LocalDate date;
}
