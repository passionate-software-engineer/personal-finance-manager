package com.pfm.filter;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilterRepository extends CrudRepository<Filter, Long> {

}
