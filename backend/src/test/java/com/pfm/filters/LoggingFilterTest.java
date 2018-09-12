package com.pfm.filters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@RunWith(MockitoJUnitRunner.class)
public class LoggingFilterTest {

  private HttpServletRequest request = mock(HttpServletRequest.class);
  private HttpServletResponse response = mock(HttpServletResponse.class);
  private ContentCachingRequestWrapper wrappedRequest = spy(new ContentCachingRequestWrapper(request));
  private ContentCachingResponseWrapper wrappedResponse = spy(new ContentCachingResponseWrapper(response));
  private FilterChain mockFilterChain = mock(FilterChain.class);
  private LoggingFilter filter = new LoggingFilter();

  @Mock
  private Appender<ILoggingEvent> mockAppender;

  @Captor
  private ArgumentCaptor<LoggingEvent> captorLoggingEvent;

  @Before
  public void setup() throws ServletException, IOException {
    // TODO why do you use logback? you should add appender to sl4j logger - there should be no dependencies to logback at all.
    final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    logger.addAppender(mockAppender);

    // TODO you can make it more readable by splitting that into multiple methods e.g. prepareHeaders(), preapareResponse() etc.
    Map<String, String> headers = new HashMap<>();
    headers.put("test1", "HTTP/1.1 200 OK");
    headers.put("Content-Type", "text/html");
    Enumeration<String> headerNames = Collections.enumeration(headers.keySet());
    Enumeration<String> headerValues = Collections.enumeration(headers.values());
    Enumeration<String> headerValues2 = Collections.enumeration(headers.values());
    when(response.getStatus()).thenReturn(200);
    when(request.getHeaderNames()).thenReturn(headerNames);
    when(request.getHeaders(anyString())).thenReturn(headerValues);
    byte[] requestContent = "Test content for request".getBytes(StandardCharsets.UTF_8);
    byte[] responseContent = "Test content for response".getBytes(StandardCharsets.UTF_8);
    when(wrappedRequest.getContentAsByteArray()).thenReturn(requestContent);
    when(wrappedResponse.getContentAsByteArray()).thenReturn(responseContent);
    when(request.getCharacterEncoding()).thenReturn("UTF-8");
    when(request.getQueryString()).thenReturn("Query sample string");
    when(request.getContentType()).thenReturn(MediaType.APPLICATION_JSON.toString());
    when(response.getCharacterEncoding()).thenReturn("UTF-8");
    when(response.getHeaderNames()).thenReturn(Collections.list(headerValues2));
    when(response.getContentType()).thenReturn(MediaType.APPLICATION_JSON.toString());
    when(request.getRequestURI()).thenReturn("/accounts");
  }

  @After
  public void teardown() {
    final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    logger.detachAppender(mockAppender);
  }

  @Test
  public void shouldFilterAndLogUnwrapped() throws ServletException, IOException {
    //given

    //when
    filter.doFilterInternal(request, response, mockFilterChain);

    //then
    verify(mockAppender, times(4)).doAppend(captorLoggingEvent.capture());

    // TODO you capture 4 times and asser only once - are you missing something? :)
    final LoggingEvent loggingEvent = captorLoggingEvent.getValue();
    assertThat(loggingEvent.getLevel(), is(Level.INFO));

    // TODO is that all info written? shouldn't we assert more?
    assertThat(loggingEvent.getFormattedMessage(), containsString("|< 200 OK"));
  }

  // TODO apply the comments from above to all methods below - you should do detailed assertions for each situation
  // TODO coverage is still not passing - you need to change in some tests the logging level as you need to cover situation when debug is on and off
  @Test
  public void shouldFilterAndLogWrapped() throws ServletException, IOException {
    //given

    //when
    filter.doFilterInternal(wrappedRequest, wrappedResponse, mockFilterChain);

    //then
    verify(mockAppender, times(6)).doAppend(captorLoggingEvent.capture());
    final LoggingEvent loggingEvent = captorLoggingEvent.getValue();
    assertThat(loggingEvent.getLevel(), is(Level.INFO));
    assertThat(loggingEvent.getFormattedMessage(), containsString("Test content for response"));
  }

  @Test
  public void shouldFilterAndLogwithNullBodyAndNotVisibleType() throws ServletException, IOException {
    //given
    when(request.getContentType()).thenReturn(MediaType.valueOf("TEST_MEDIA_TYPE/TEST").toString());
    when(request.getQueryString()).thenReturn(null);

    //when
    filter.doFilterInternal(wrappedRequest, wrappedResponse, mockFilterChain);

    //then
    verify(mockAppender, times(6)).doAppend(captorLoggingEvent.capture());
    final LoggingEvent loggingEvent = captorLoggingEvent.getValue();
    assertThat(loggingEvent.getLevel(), is(Level.INFO));
    assertThat(loggingEvent.getFormattedMessage(), containsString("Test content for response"));
  }

  @Test
  public void shouldFilterAndThrowUnsupportedEncodingException() throws ServletException, IOException {
    //given
    when(response.getCharacterEncoding()).thenReturn("TEST_ENCODING");

    //when
    filter.doFilterInternal(wrappedRequest, wrappedResponse, mockFilterChain);

    //then
    verify(mockAppender, times(7)).doAppend(captorLoggingEvent.capture());
    final LoggingEvent loggingEvent = captorLoggingEvent.getValue();
    assertThat(loggingEvent.getLevel(), is(Level.WARN));
    assertThat(loggingEvent.getFormattedMessage(), containsString("Not able to convert response"));
  }

}