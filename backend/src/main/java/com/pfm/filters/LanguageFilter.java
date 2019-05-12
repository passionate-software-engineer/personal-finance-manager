package com.pfm.filters;

import com.pfm.config.MessagesProvider;
import com.pfm.config.MessagesProvider.Language;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@Order(3)
public class LanguageFilter extends OncePerRequestFilter {

  public static final String LANGUAGE_HEADER = "Language";

  @Override
  protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
      throws IOException, ServletException {
    try {
      setLanguageForProcessingThread(request);
      filterChain.doFilter(request, response);
    } finally {
      MessagesProvider.setLanguage(null);
    }
  }

  private void setLanguageForProcessingThread(HttpServletRequest request) {
    String languageHeader = request.getHeader(LANGUAGE_HEADER);

    if (languageHeader == null) {
      languageHeader = "en";
    }

    MessagesProvider.setLanguage(Language.getEnumByString(languageHeader));
  }

}
