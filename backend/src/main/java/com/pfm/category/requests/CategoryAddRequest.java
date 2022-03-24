package com.pfm.category.requests;

import com.pfm.category.validation.UniqueName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class CategoryAddRequest extends CategoryRequestBase {

  @Schema(description = "CATEGORY name", required = true, example = "Eating out")
  @UniqueName
  private String name;

}
