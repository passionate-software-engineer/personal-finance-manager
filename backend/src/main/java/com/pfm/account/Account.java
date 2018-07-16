package com.pfm.account;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Account {

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.AUTO)
  @ApiModelProperty(value = "The database generated account ID.", required = true, example = "22")
  private Long id;

  @NotNull
  @Column(unique = true)
  @ApiModelProperty(value = "Account name", required = true, example = "Alior")
  private String name;

  @NotNull
  @ApiModelProperty(value = "Account's balance", required = true, example = "12345")
  private BigDecimal balance;
}
