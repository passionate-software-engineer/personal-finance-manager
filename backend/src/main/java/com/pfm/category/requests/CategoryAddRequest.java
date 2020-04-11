package com.pfm.category.requests;

import com.pfm.category.validation.UniqueName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class CategoryAddRequest extends CategoryRequestBase {

  @ApiModelProperty(value = "CATEGORY name", required = true, example = "Eating out")
  @UniqueName
  private String name;

}
