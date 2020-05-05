package com.pfm.export.converter;

import com.pfm.currency.Currency;
import com.pfm.currency.CurrencyService;
import com.pfm.export.ExportResult.ExportAccount;
import com.pfm.export.ExportResult.ExportFundsSummary;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FundsExporter {

  private final CurrencyService currencyService;

  public ExportFundsSummary export(final List<ExportAccount> accounts, long userId) {
    final BigDecimal sumOfAllFunds = calculateBalanceSum(accounts, userId);
    final Map<String, BigDecimal> currencyToBalanceMap = calculateBalanceForCurrency(accounts, userId);

    return ExportFundsSummary.builder()
        .sumOfAllFundsInBaseCurrency(sumOfAllFunds)
        .currencyToFundsMap(currencyToBalanceMap)
        .build();
  }

  private BigDecimal calculateBalanceSum(final List<ExportAccount> accounts, long userId) {
    Map<String, BigDecimal> currencyToMap = currencyService.getCurrencies(userId).stream()
        .collect(Collectors.toMap(Currency::getName, Currency::getExchangeRate));

    return accounts.stream()
        .map(account -> account.getBalance().multiply(currencyToMap.get(account.getCurrency())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private Map<String, BigDecimal> calculateBalanceForCurrency(final List<ExportAccount> accounts, long userId) {
    Map<String, BigDecimal> currencyToBalanceMap = new HashMap<>();

    accounts.forEach(account -> currencyToBalanceMap
        .put(account.getCurrency(), updateBalance(currencyToBalanceMap, account)));

    currencyService.getCurrencies(userId)
        .forEach(currency -> currencyToBalanceMap.putIfAbsent(currency.getName(), BigDecimal.ZERO));

    return currencyToBalanceMap;
  }

  private BigDecimal updateBalance(final Map<String, BigDecimal> currencyToBalanceMap, final ExportAccount account) {
    return currencyToBalanceMap.getOrDefault(account.getCurrency(), BigDecimal.ZERO).add(account.getBalance());
  }

}
