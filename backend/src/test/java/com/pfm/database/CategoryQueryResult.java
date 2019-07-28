package com.pfm.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryQueryResult implements Diffable<CategoryQueryResult> {

  private String name;

  private String parentCategory;

  @Override
  public DiffResult diff(CategoryQueryResult obj) {
    return new DiffBuilder(this, obj, ToStringStyle.SHORT_PREFIX_STYLE)
        .append("name", this.name, obj.name)
        .append("parentCategory", this.parentCategory, obj.parentCategory)
        .build();
  }
}
