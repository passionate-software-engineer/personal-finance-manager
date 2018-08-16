package com.pfm.category;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {

  //TODO add test for scenario where two users add same name for Category
  @Query(value = "select count(category.parentCategory.id) from Category category where category.parentCategory.id = :id")
  Integer numberOfEntriesUsingThisCategoryAsParentId(@Param("id") long id);

  // TODO - is that query good? If you pass Domek and Dom it will find it as duplicate!
  @Query("select c from Category c where lower(c.name) like lower(concat('%', :nameToFind,'%')) AND c.userId = :id")
  List<Category> findByNameIgnoreCaseAndUserId(@Param("nameToFind") String name, @Param("id") long id);

  List<Category> findByUserId(long userId);

  Optional<Category> findByIdAndUserId(long id, long userId);
}