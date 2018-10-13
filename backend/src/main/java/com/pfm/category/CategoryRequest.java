package com.pfm.category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true) // TODO we should enable strict validation - don't allow additional fields (check for other occurrences)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class CategoryRequest {

  @ApiModelProperty(value = "Parent category id", example = "1")
  private Long parentCategoryId;

  @ApiModelProperty(value = "Category name", required = true, example = "Eating out")
  private String name;
}