package com.pfm.category;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {

} // TODO flyway is missing table for categories, in flyway remove V2 - this account should not be added at startup, looks like flyway is not used but should be
