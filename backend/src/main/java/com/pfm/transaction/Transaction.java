package com.pfm.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
  @Column(unique = true)
  @ApiModelProperty(value = "Description", required = true, example = "Cinema")
  private String description;

  @NotNull
  @ApiModelProperty(value = "Category", required = true, example = "Entertainment")
  private String category;

  @NotNull
  @ApiModelProperty(value = "Price", required = true, example = "15")
  private BigDecimal price;

  @NotNull
  @ApiModelProperty(value = "Account", required = true, example = "Mbank")
  private String account;
}

