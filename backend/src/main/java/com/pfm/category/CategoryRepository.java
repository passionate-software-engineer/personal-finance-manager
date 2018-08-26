package com.pfm.category;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {

  @Query(value = "select count(category.parentCategory.id) from Category category where category.parentCategory.id = :id")
  Integer numberOfEntriesUsingThisCategoryAsParentId(@Param("id") long id);

  List<Category> findByNameIgnoreCase(String name);
}