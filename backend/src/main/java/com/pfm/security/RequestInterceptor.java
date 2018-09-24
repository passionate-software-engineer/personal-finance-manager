package com.pfm.security;

import com.pfm.auth.TokenService;
import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
@CrossOrigin
public class RequestInterceptor extends OncePerRequestFilter {

  @Autowired
  TokenService tokenService;

  private static final String pattern = "(\\/users.*|\\/swagger.*)";

  private final Logger logger = LoggerFactory.getLogger("Security");

//  @Override
//  public boolean preHandle(HttpServletRequest request,
//      HttpServletResponse response, Object object) throws Exception {
//
//    logger.error(request.getHeaderNames().toString());
//
//    if (request.getRequestURI().matches(pattern)) {
//      return true;
//    }
//
//    Enumeration<String> headerNames = request.getHeaderNames();
//
//    String requestToken = request.getHeader("authorization");
//    String requestToken1 = request.getHeader("Authorization");
//    if (requestToken == null || requestToken.isEmpty()) {
//      logger.error("No request token.");
//      response.getWriter().write("Incorrect Token");
//      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//      return false;
//    }
//
//    if (isTokenCorrect(requestToken)) {
//      return true;
//    } else {
//      logger.error("Request token is incorrect.");
//      response.getWriter().write("Incorrect Token");
//      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//      return false;
//    }
//  }

  private boolean isTokenCorrect(String token) {
    return tokenService.validateToken(token);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    logger.error(request.getHeaderNames().toString());

    if (request.getRequestURI().matches(pattern)) {

    }

    Enumeration<String> headerNames = request.getHeaderNames();

    String requestToken = request.getHeader("authorization");
    String requestToken1 = request.getHeader("Authorization");
    if (requestToken == null || requestToken.isEmpty()) {
      logger.error("No request token.");
      response.getWriter().write("Incorrect Token");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    }

    if (isTokenCorrect(requestToken)) {

    } else {
      logger.error("Request token is incorrect.");
      response.getWriter().write("Incorrect Token");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    }
  }

  private static boolean isOkRequest(String pattern, String stringToCheck) {
    return pattern.matches(stringToCheck);
  }
}
