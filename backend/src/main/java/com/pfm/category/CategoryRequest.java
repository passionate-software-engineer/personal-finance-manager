package com.pfm.category;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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