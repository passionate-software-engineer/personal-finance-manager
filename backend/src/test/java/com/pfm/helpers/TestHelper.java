package com.pfm.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TestHelper {

  public static BigDecimal convertDoubleToBigDecimal(double amount) {
    return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
  }

}
