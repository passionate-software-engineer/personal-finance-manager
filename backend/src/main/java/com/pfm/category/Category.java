package com.pfm.category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Category {

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.AUTO)
  @ApiModelProperty(value = "The database generated category ID.", required = true, example = "23")
  private Long id;

  @NotNull
  @ApiModelProperty(value = "Category name", required = true, example = "Car")
  private String name;

  @ManyToOne
  @ApiModelProperty(value = "Parent category object", required = true)
  private Category parentCategory;
}
