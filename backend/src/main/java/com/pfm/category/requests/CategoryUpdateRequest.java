package com.pfm.category.requests;

import com.pfm.category.validation.UniqueName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@SuperBuilder
@Getter
@NoArgsConstructor
public class CategoryUpdateRequest extends CategoryRequestBase {

  @ApiModelProperty(value = "CATEGORY name", required = true, example = "Eating out")
  @UniqueName(requestType = RequestType.UPDATE)
  private String name;

}
