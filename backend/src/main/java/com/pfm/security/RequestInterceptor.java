package com.pfm.security;

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

  @Autowired
  TokenService tokenService;

  private static final String pattern = "(\\/users.*|\\/swagger.*)";
  private final Logger logger = LoggerFactory.getLogger("Security");

  @Override
  public boolean preHandle(HttpServletRequest request,
      HttpServletResponse response, Object object) throws Exception {

    if ("OPTIONS".equals(request.getMethod())) {
      return true;
    }

    if (request.getRequestURI().matches(pattern)) {
      return true;
    }

    String requestToken = request.getHeader("authorization");
    if (requestToken == null || requestToken.isEmpty()) {
      logger.error("No request token.");
      response.getWriter().write("Incorrect Token");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return false;
    }

    if (isTokenCorrect(requestToken)) {
      long userIdFromToken = tokenService.getUserIdFromToken(requestToken);
      request.setAttribute("userId", userIdFromToken);
      return true;
    } else {
      logger.error("Request token is incorrect.");
      response.getWriter().write("Incorrect Token");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return false;
    }
  }

  private boolean isTokenCorrect(String token) {
    return tokenService.validateToken(token);
  }

}
