package com.pfm.swagger;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiConstants {

  public static final String OK_MESSAGE = "OK";
  public static final String BAD_REQUEST_MESSAGE = "Bad request";
  public static final String UNAUTHORIZED_MESSAGE = "Unauthorized";
  public static final String NOT_FOUND_MESSAGE = "Not found";

  public static final String BEARER = "Bearer";
  public static final String CONTAINER_LIST = "list";
}
