package com.pfm.transactions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transactions {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(value = "Transaction id", required = true, example = "1")
    private Long transaction_id;

    @NotNull
    @Column(unique = true)
    @ApiModelProperty(value = "Description", required = true, example = "smart watch")
    private String transaction_description;

    @NotNull
    @ApiModelProperty(value = "Category", required = true, example = "Swiss")
    private String transaction_category;

    @NotNull
    @ApiModelProperty(value = "account", required = true, example = "My account")
    private BigDecimal transaction_account;
}

