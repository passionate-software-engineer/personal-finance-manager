package com.pfm.filters;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@Order(1)
public class CorrelationIdFilter extends OncePerRequestFilter {

  static final String CORRELATION_ID = "correlationId"; // TODO change to Correlation-Id to be consistent with other headers convention

  @Override
  protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
      throws IOException, ServletException {
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