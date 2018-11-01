package com.pfm.security;

import com.google.common.net.HttpHeaders;
import com.pfm.auth.TokenService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
@CrossOrigin
public class RequestInterceptor extends HandlerInterceptorAdapter {

  //TODO this should be done be spring security
  private static final String pattern = "(\\/users.*|.*swagger.*)";
  private final Logger logger = LoggerFactory.getLogger(RequestInterceptor.class.getName());

  @Autowired
  private TokenService tokenService;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {

    if ("OPTIONS".equals(request.getMethod())) {
      return true;
    }

    if (request.getRequestURI().matches(pattern)) {
      return true;
    }

    String requestToken = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (requestToken == null || requestToken.isEmpty()) {
      logger.error("Authorization header value is empty");
      response.setContentType("text/plain");
      response.getWriter().write("Authorization header value is empty");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setHeader("Access-Control-Allow-Origin", "*");
      return false;
    }

    if (isTokenCorrect(requestToken)) {
      long userIdFromToken = tokenService.getUserIdBasedOnToken(requestToken);
      request.setAttribute("userId", userIdFromToken);
      return true;
    } else {
      logger.error("Request token " + requestToken + " is incorrect");
      response.setContentType("text/plain");
      response.getWriter().write("Request token " + requestToken + " is incorrect");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setHeader("Access-Control-Allow-Origin", "*");
      return false;
    }
  }

  private boolean isTokenCorrect(String token) {
    return tokenService.validateToken(token);
  }

}
