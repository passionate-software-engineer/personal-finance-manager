package com.pfm.category;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {

  @Query(value = "select count(category.parentCategory.id) from Category category where category.parentCategory.id = :id")
  Integer numberOfEntriesUsingThisCategoryAsParentId(@Param("id") long id);

  @Query("select category from Category category where lower(category.name) like lower(:nameToFind) AND category.userId = :id")
  List<Category> findByNameIgnoreCaseAndUserId(@Param("nameToFind") String name, @Param("id") long id);

  List<Category> findByUserId(long userId);

  Optional<Category> findByIdAndUserId(long id, long userId);

  boolean existsByIdAndUserId(long categoryId, long userId);
}