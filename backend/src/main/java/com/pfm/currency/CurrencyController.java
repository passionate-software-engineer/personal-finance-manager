package com.pfm.currency;

import com.pfm.auth.UserProvider;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class CurrencyController implements CurrencyApi {

  private CurrencyService currencyService;
  private UserProvider userProvider;

  @Override
  public ResponseEntity<List<Currency>> getCurrencies() {
    long userId = userProvider.getCurrentUserId();

    log.info("Returning list of currencies for user {}", userId);

    List<Currency> currencies = currencyService.getCurrencies(userId);

    return ResponseEntity.ok(currencies);
  }
}
