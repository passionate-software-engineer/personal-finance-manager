package com.pfm.category.requests;

import static com.pfm.config.MessagesProvider.EMPTY_CATEGORY_NAME;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
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
  @NotBlank(message = EMPTY_CATEGORY_NAME)
  private String name;

}
