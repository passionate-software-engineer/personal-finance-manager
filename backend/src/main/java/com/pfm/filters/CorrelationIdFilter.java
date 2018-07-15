package com.pfm.filters;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws IOException, ServletException {
    try {
      addRequestCorrelationIdToMDC(request);
      filterChain.doFilter(request, response);
    } finally {
      removeRequestCorrelationIdFromMDC();
    }

  }

  private String addRequestCorrelationIdToMDC(HttpServletRequest request) {
    String correlationId = request.getHeader("correlationId");

    if (correlationId == null) {
      correlationId = UUID.randomUUID().toString();
    }

    MDC.put("correlationId", correlationId);

    return correlationId;
  }

  private void removeRequestCorrelationIdFromMDC() {
    MDC.remove("correlationId");
  }

}
