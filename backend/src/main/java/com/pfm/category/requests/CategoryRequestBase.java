package com.pfm.category.requests;

import static com.pfm.config.MessagesProvider.CATEGORY_PRIORITY_WRONG_VALUE;

import com.pfm.category.validation.CategoryExistsIfProvided;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Setter
@Getter
@SuperBuilder
public abstract class CategoryRequestBase {

  @ApiModelProperty(value = "Parent category id", example = "1")
  @CategoryExistsIfProvided
  private Long parentCategoryId;

  @ApiModelProperty(value = "CATEGORY priority", required = true, example = "1")
  @Builder.Default
  @Min(value = 1, message = CATEGORY_PRIORITY_WRONG_VALUE)
  @Max(value = 1000, message = CATEGORY_PRIORITY_WRONG_VALUE)
  private int priority = 1000;

  public abstract String getName();

  public abstract void setName(String name);
}
