package com.pfm.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalHelper {

  public static String convertBigDecimalToString(BigDecimal bigDecimal) {
    return bigDecimal.setScale(2, RoundingMode.HALF_UP).toString();
  }

}
