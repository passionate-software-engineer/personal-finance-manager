package com.pfm.filter;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class FilterService {

  private FilterRepository filterRepository;

  public Filter addFilter(long userId, Filter filter) {
    filter.setUserId(userId);
    if (filter.isDefault()) {
      checkAnyFilterIsDefault(getAllFilters(userId));
    }
    return filterRepository.save(filter);
  }

  public Optional<Filter> getFilterByIdAndUserId(long id, long userId) {
    return filterRepository.findByIdAndUserId(id, userId);
  }

  public void deleteFilter(long id) {
    filterRepository.deleteById(id);
  }

  public List<Filter> getAllFilters(long userId) {
    return filterRepository.findByUserId(userId).stream()
        .sorted(Comparator.comparing(Filter::getId))
        .collect(Collectors.toList());
  }

  public void updateFilter(long id, long userId, Filter filter) {
    Filter filterToUpdate = getFilterFromDatabase(id, userId);

    filterToUpdate.setAccountIds(filter.getAccountIds());
    filterToUpdate.setCategoryIds(filter.getCategoryIds());
    filterToUpdate.setDateFrom(filter.getDateFrom());
    filterToUpdate.setDateTo(filter.getDateTo());
    filterToUpdate.setPriceFrom(filter.getPriceFrom());
    filterToUpdate.setPriceTo(filter.getPriceTo());
    filterToUpdate.setDescription(filter.getDescription());
    filterToUpdate.setName(filter.getName());
    if (filter.isDefault() && !filterToUpdate.isDefault()) {
      checkAnyFilterIsDefault(getAllFilters(userId));
    }
    filterToUpdate.setDefault(filter.isDefault());
    filterRepository.save(filterToUpdate);
  }

  private Filter getFilterFromDatabase(long id, long userId) {
    Optional<Filter> filterFromDb = getFilterByIdAndUserId(id, userId);

    if (!filterFromDb.isPresent()) {
      throw new IllegalStateException("Filter with id: " + id + " does not exist in database");
    }

    return filterFromDb.get();
  }

  public boolean filterExistByAccountId(long accountId) {
    return filterRepository.existsByAccountIdsContains(accountId);
  }

  public boolean filterExistByCategoryId(long categoryId) {
    return filterRepository.existsByCategoryIdsContains(categoryId);
  }

  public void checkAnyFilterIsDefault(List<Filter> loadFiltersList) {
    for (Filter filter : loadFiltersList) {
      if (filter.isDefault()) {
        setUpFilterIsNotDefault(filter);
        filterRepository.save(filter);
        break;
      }
    }
  }

  public void setUpFilterIsNotDefault(Filter filter) {
    filter.setAsNotDefault();
  }
}
