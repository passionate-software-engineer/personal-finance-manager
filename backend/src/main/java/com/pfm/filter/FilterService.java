package com.pfm.filter;

import com.pfm.account.AccountService;
import com.pfm.category.CategoryService;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class FilterService {

  private FilterRepository filterRepository;
  private AccountService accountService;
  private CategoryService categoryService;

  public Filter addFilter(Filter filter) {

    return filterRepository.save(filter);
  }

  public Optional<Filter> getFilterById(long id) {
    return filterRepository.findById(id);
  }

  public void deleteFilter(long id) {
    filterRepository.deleteById(id);
  }

  public List<Filter> getAllFilters() {
    return StreamSupport.stream(filterRepository.findAll().spliterator(), false)
        .sorted(Comparator.comparing(Filter::getId))
        .collect(Collectors.toList());
  }

}
