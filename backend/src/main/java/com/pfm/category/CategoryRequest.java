package com.pfm.category;

import static com.pfm.config.MessagesProvider.CATEGORY_PRIORITY_WRONG_VALUE;
import static com.pfm.config.MessagesProvider.EMPTY_CATEGORY_NAME;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
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

  @ApiModelProperty(value = "CATEGORY name", required = true, example = "Eating out")
  @NotBlank(message = EMPTY_CATEGORY_NAME)
  private String name;

  @ApiModelProperty(value = "CATEGORY priority", required = true, example = "1")
  @Builder.Default
  @Min(value = 1, message = CATEGORY_PRIORITY_WRONG_VALUE)
  @Max(value = 1000, message = CATEGORY_PRIORITY_WRONG_VALUE)
  private int priority = 1000;
}
