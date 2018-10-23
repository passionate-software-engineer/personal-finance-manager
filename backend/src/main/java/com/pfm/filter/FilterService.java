package com.pfm.filter;

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

  public Filter addFilter(long userId, Filter filter) {
    filter.setUserId(userId);
    return filterRepository.save(filter);
  }

  public Optional<Filter> getFilterByIdAndUserId(long id, long userId) {
    return filterRepository.findByIdAndUserId(id, userId);
  }

  public void deleteFilter(long id) {
    filterRepository.deleteById(id);
  }

  public List<Filter> getAllFilters(long userId) {
    return StreamSupport.stream(filterRepository.findByUserId(userId).spliterator(), false)
        .sorted(Comparator.comparing(Filter::getId))
        .collect(Collectors.toList());
  }

  public void updateFilter(long id, long userId, Filter filter) {
    Filter filterToUpdate = getFilterFromDatabase(id, userId);

    // TODO add history entries for filter modifications.

    filterToUpdate.setAccountIds(filter.getAccountIds());
    filterToUpdate.setCategoryIds(filter.getCategoryIds());
    filterToUpdate.setDateFrom(filter.getDateFrom());
    filterToUpdate.setDateTo(filter.getDateTo());
    filterToUpdate.setPriceFrom(filter.getPriceFrom());
    filterToUpdate.setPriceTo(filter.getPriceTo());
    filterToUpdate.setDescription(filter.getDescription());
    filterToUpdate.setName(filter.getName());

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

  public boolean filterExistByFilterIdAndUserId(long filterId, long userId) {
    return filterRepository.existsByIdAndUserId(filterId, userId);
  }
}
