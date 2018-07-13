package com.pfm.category;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {

  @Query(value = "select count(c.parentCategory.id) from Category c where c.parentCategory.id = :id")
  public Integer parentCategoryNumber(@Param("id") long id);


} // TODO flyway is missing table for categories, in flyway remove V2 - this account should not be added at startup, looks like flyway is not used but should be
