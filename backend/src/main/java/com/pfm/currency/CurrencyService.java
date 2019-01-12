package com.pfm.currency;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class CurrencyService {

  private CurrencyRepository currencyRepository;

  public Currency getCurrencyByIdAndUserId(long currencyId, long userId) {
    Optional<Currency> currencyOptional = currencyRepository.findByIdAndUserId(currencyId, userId);
    if (!currencyOptional.isPresent()) {
      throw new IllegalStateException("Currency with id: " + currencyId + " does not exist in database");
    }
    return currencyOptional.get();
  }

  public List<Currency> getCurrencies(long userId) {
    return currencyRepository.findByUserId(userId).stream()
        .sorted(Comparator.comparing(Currency::getName))
        .collect(Collectors.toList());
  }

  public void addDefaultCurrencies(long userId) {
    currencyRepository.save(Currency.builder().name("PLN").exchangeRate(BigDecimal.valueOf(100, 2)).userId(userId).build());
    currencyRepository.save(Currency.builder().name("USD").exchangeRate(BigDecimal.valueOf(358, 2)).userId(userId).build());
    currencyRepository.save(Currency.builder().name("EUR").exchangeRate(BigDecimal.valueOf(424, 2)).userId(userId).build());
    currencyRepository.save(Currency.builder().name("GBP").exchangeRate(BigDecimal.valueOf(499, 2)).userId(userId).build());
  }

}