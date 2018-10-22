package com.pfm.history;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryEntryRepository extends CrudRepository<HistoryEntry, Long> {

  List<HistoryEntry> findByUserId(long userId);

}
