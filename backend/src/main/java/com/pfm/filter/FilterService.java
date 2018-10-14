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

  public Filter addFilter(Filter filter) {
    return filterRepository.save(filter);
  }

  public Optional<Filter> getFilterById(long id) {
    return filterRepository.findById(id);
  }

  //TODO possibly replace this method everywhere to use only "get.......ByIdAndUserId(long id, long userId)" to make app safer ??
  //Its used sometimes in places where validation is done e.g. in validator
  public Optional<Filter> getFilterByIdAndByUserId(long id, long userId) {
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

  public void updateFilter(long id, Filter filter) {
    Filter filterToUpdate = getFilterFromDatabase(id);

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

  private Filter getFilterFromDatabase(long id) {
    Optional<Filter> filterFromDb = getFilterById(id);

    if (!filterFromDb.isPresent()) {
      throw new IllegalStateException("Filter with id: " + id + " does not exist in database");
    }

    return filterFromDb.get();
  }
}
