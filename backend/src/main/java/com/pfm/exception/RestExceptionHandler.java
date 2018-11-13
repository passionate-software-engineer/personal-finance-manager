package com.pfm.exception;

import com.pfm.config.MessagesProvider;
import com.pfm.filters.CorrelationIdFilter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// TODO handle validation this way - throw custom exception and return response from here - no need to add if's in controllers
@Slf4j
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {Exception.class})
  public ResponseEntity<Object> handleUnhandledException(Exception exception, WebRequest request) {
    log.error("Internal error", exception);

    String bodyOfResponse = String.format(
        MessagesProvider.getMessage(MessagesProvider.INTERNAL_ERROR),
        CorrelationIdFilter.getCorrelationId(),
        ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
    );

    return handleExceptionInternal(exception, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

}