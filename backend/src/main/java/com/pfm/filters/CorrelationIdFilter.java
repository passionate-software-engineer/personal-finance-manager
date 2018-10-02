package com.pfm.filters;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@Order(value = 3)
public class CorrelationIdFilter extends OncePerRequestFilter {

  public static final String CORRELATION_ID = "correlationId";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws IOException, ServletException {
    try {
      addRequestCorrelationIdToMdc(request);
      filterChain.doFilter(request, response);
    } finally {
      removeRequestCorrelationIdFromMdc();
    }
  }

  private void addRequestCorrelationIdToMdc(HttpServletRequest request) {
    String correlationId = request.getHeader(CORRELATION_ID);

    if (correlationId == null) {
      correlationId = UUID.randomUUID().toString();
    }

    MDC.put(CORRELATION_ID, correlationId);
  }

  private void removeRequestCorrelationIdFromMdc() {
    MDC.remove(CORRELATION_ID);
  }
}