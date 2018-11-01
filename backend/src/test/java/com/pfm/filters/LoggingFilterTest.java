package com.pfm.filters;

import static com.pfm.filters.LoggingFilter.REQUEST_MARKER;
import static com.pfm.filters.LoggingFilter.RESPONSE_MARKER;
import static java.util.Collections.enumeration;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
import java.util.HashMap;
import java.util.List;
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

  private static final String ENCODING_HEADER = "Encoding";
  private static final String CONTENT_TYPE_HEADER = "Content-Type";
  private static final String REQUEST_METHOD = "GET";
  private static final String REQUEST_URI = "/accounts";
  private static final String REQUEST_CONTENT_TYPE = MediaType.APPLICATION_JSON.toString();
  private static final String REQUEST_QUERY_STRING = "Query string";
  private static final String REQUEST_ENCODING = "UTF-8";
  private static final Map<String, String> REQUEST_HEADERS;
  private static final String REQUEST_CONTENT = "Test content for request";
  private static final String RESPONSE_CONTENT = "Test content for response";

  static {
    REQUEST_HEADERS = new HashMap<>();
    REQUEST_HEADERS.put(ENCODING_HEADER, REQUEST_ENCODING);
    REQUEST_HEADERS.put(CONTENT_TYPE_HEADER, REQUEST_CONTENT_TYPE);
  }

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
  public void prepareRequests() {
    when(request.getHeaderNames()).thenReturn(enumeration(REQUEST_HEADERS.keySet()));
    when(request.getHeaders(anyString())).thenReturn(enumeration(REQUEST_HEADERS.values()));

    byte[] requestContent = REQUEST_CONTENT.getBytes(StandardCharsets.UTF_8);
    when(wrappedRequest.getContentAsByteArray()).thenReturn(requestContent);

    when(request.getCharacterEncoding()).thenReturn(REQUEST_ENCODING);
    when(request.getQueryString()).thenReturn(REQUEST_QUERY_STRING);
    when(request.getContentType()).thenReturn(REQUEST_CONTENT_TYPE);
    when(request.getHeaders(ENCODING_HEADER)).thenReturn(enumeration(Collections.singletonList(REQUEST_ENCODING)));
    when(request.getHeaders(CONTENT_TYPE_HEADER)).thenReturn(enumeration(Collections.singletonList(REQUEST_CONTENT_TYPE)));
    when(request.getRequestURI()).thenReturn(REQUEST_URI);
    when(request.getMethod()).thenReturn(REQUEST_METHOD);
  }

  @Before
  public void prepareResponses() {
    byte[] responseContent = RESPONSE_CONTENT.getBytes(StandardCharsets.UTF_8);
    when(wrappedResponse.getContentAsByteArray()).thenReturn(responseContent);

    when(response.getStatus()).thenReturn(200);
    when(response.getCharacterEncoding()).thenReturn(StandardCharsets.UTF_8.name());
    when(response.getHeaderNames()).thenReturn(Collections.list(enumeration(REQUEST_HEADERS.values())));
    when(response.getContentType()).thenReturn(MediaType.APPLICATION_JSON.toString());
  }

  @Before
  public void prepareLogger() {
    final Logger logger = (Logger) LoggerFactory.getLogger(LoggingFilter.class);
    logger.addAppender(mockAppender);
    logger.setLevel(Level.DEBUG);
  }

  @After
  public void tearDown() {
    final Logger logger = (Logger) LoggerFactory.getLogger(LoggingFilter.class);
    logger.detachAppender(mockAppender);
  }

  @Test
  public void shouldProcessNotWrappedRequest() throws ServletException, IOException {
    //given

    //when
    filter.doFilterInternal(request, response, mockFilterChain);

    //then
    verify(mockAppender, times(4)).doAppend(captorLoggingEvent.capture());
    final List<LoggingEvent> resultLog = captorLoggingEvent.getAllValues();

    assertThat(resultLog, hasSize(4));

    LoggingEvent loggingEvent = resultLog.get(0);
    assertThat(loggingEvent.getLevel(), is(Level.INFO));
    assertThat(loggingEvent.getFormattedMessage(),
        is(String.format("%s %s %s?%s", REQUEST_MARKER, REQUEST_METHOD, REQUEST_URI, REQUEST_QUERY_STRING)));

    loggingEvent = resultLog.get(1);
    assertThat(loggingEvent.getLevel(), is(Level.DEBUG));
    assertThat(loggingEvent.getFormattedMessage(), is(String.format("%s %s: %s", REQUEST_MARKER, ENCODING_HEADER, REQUEST_ENCODING)));

    loggingEvent = resultLog.get(2);
    assertThat(loggingEvent.getLevel(), is(Level.DEBUG));
    assertThat(loggingEvent.getFormattedMessage(), is(String.format("%s %s: %s", REQUEST_MARKER, CONTENT_TYPE_HEADER, REQUEST_CONTENT_TYPE)));

    loggingEvent = resultLog.get(3);
    assertThat(loggingEvent.getLevel(), is(Level.INFO));
    assertThat(loggingEvent.getFormattedMessage(), containsString(String.format("%s %d OK", RESPONSE_MARKER, response.getStatus())));
  }

  @Test
  public void shouldProcessWrappedRequest() throws ServletException, IOException {
    //given

    //when
    filter.doFilterInternal(wrappedRequest, wrappedResponse, mockFilterChain);

    //then
    verify(mockAppender, times(6)).doAppend(captorLoggingEvent.capture());
    final List<LoggingEvent> resultLog = captorLoggingEvent.getAllValues();

    assertThat(resultLog, hasSize(6));

    LoggingEvent loggingEvent = resultLog.get(0);
    assertThat(loggingEvent.getLevel(), is(Level.INFO));
    assertThat(loggingEvent.getFormattedMessage(),
        is(String.format("%s %s %s?%s", REQUEST_MARKER, REQUEST_METHOD, REQUEST_URI, REQUEST_QUERY_STRING)));

    loggingEvent = resultLog.get(1);
    assertThat(loggingEvent.getLevel(), is(Level.DEBUG));
    assertThat(loggingEvent.getFormattedMessage(), is(String.format("%s %s: %s", REQUEST_MARKER, ENCODING_HEADER, REQUEST_ENCODING)));

    loggingEvent = resultLog.get(2);
    assertThat(loggingEvent.getLevel(), is(Level.DEBUG));
    assertThat(loggingEvent.getFormattedMessage(), is(String.format("%s %s: %s", REQUEST_MARKER, CONTENT_TYPE_HEADER, REQUEST_CONTENT_TYPE)));

    loggingEvent = resultLog.get(3);
    assertThat(loggingEvent.getLevel(), is(Level.INFO));
    assertThat(loggingEvent.getFormattedMessage(), is(String.format("%s %n %s", REQUEST_MARKER, REQUEST_CONTENT)));

    loggingEvent = resultLog.get(4);
    assertThat(loggingEvent.getLevel(), is(Level.INFO));
    assertThat(loggingEvent.getFormattedMessage(), containsString(String.format("%s %d OK", RESPONSE_MARKER, response.getStatus())));

    loggingEvent = resultLog.get(5);
    assertThat(loggingEvent.getLevel(), is(Level.INFO));
    assertThat(loggingEvent.getFormattedMessage(), is(String.format("%s %n %s", RESPONSE_MARKER, RESPONSE_CONTENT)));
  }

  @Test
  public void shouldLogContentLengthWhenInvisibleContentTypeWasProvided() throws ServletException, IOException {
    //given
    when(request.getContentType()).thenReturn(MediaType.APPLICATION_OCTET_STREAM.toString());
    when(request.getQueryString()).thenReturn(null);

    //when
    filter.doFilterInternal(wrappedRequest, wrappedResponse, mockFilterChain);

    //then
    verify(mockAppender, times(6)).doAppend(captorLoggingEvent.capture());
    final List<LoggingEvent> resultLog = captorLoggingEvent.getAllValues();

    assertThat(resultLog, hasSize(6));

    LoggingEvent loggingEvent = resultLog.get(0);
    assertThat(loggingEvent.getLevel(), is(Level.INFO));
    assertThat(loggingEvent.getFormattedMessage(),
        is(String.format("%s %s %s", REQUEST_MARKER, REQUEST_METHOD, REQUEST_URI)));

    loggingEvent = resultLog.get(1);
    assertThat(loggingEvent.getLevel(), is(Level.DEBUG));
    assertThat(loggingEvent.getFormattedMessage(), is(String.format("%s %s: %s", REQUEST_MARKER, ENCODING_HEADER, REQUEST_ENCODING)));

    loggingEvent = resultLog.get(2);
    assertThat(loggingEvent.getLevel(), is(Level.DEBUG));
    assertThat(loggingEvent.getFormattedMessage(), is(String.format("%s %s: %s", REQUEST_MARKER, CONTENT_TYPE_HEADER, REQUEST_CONTENT_TYPE)));

    loggingEvent = resultLog.get(3);
    assertThat(loggingEvent.getLevel(), is(Level.INFO));
    assertThat(loggingEvent.getFormattedMessage(), is(String.format("%s [%d bytes content]", REQUEST_MARKER, REQUEST_CONTENT.length())));

    loggingEvent = resultLog.get(4);
    assertThat(loggingEvent.getLevel(), is(Level.INFO));
    assertThat(loggingEvent.getFormattedMessage(), containsString(String.format("%s %d OK", RESPONSE_MARKER, response.getStatus())));

    loggingEvent = resultLog.get(5);
    assertThat(loggingEvent.getLevel(), is(Level.INFO));
    assertThat(loggingEvent.getFormattedMessage(), is(String.format("%s %n %s", RESPONSE_MARKER, RESPONSE_CONTENT)));
  }

  @Test
  public void shouldHandleUnsupportedEncodingException() throws ServletException, IOException {
    //given
    when(response.getCharacterEncoding()).thenReturn("NOT_EXISTING_ENCODING");

    //when
    filter.doFilterInternal(wrappedRequest, wrappedResponse, mockFilterChain);

    //then
    verify(mockAppender, times(7)).doAppend(captorLoggingEvent.capture());
    final List<LoggingEvent> resultLog = captorLoggingEvent.getAllValues();

    assertThat(resultLog, hasSize(7));

    LoggingEvent loggingEvent = resultLog.get(0);
    assertThat(loggingEvent.getLevel(), is(Level.INFO));
    assertThat(loggingEvent.getFormattedMessage(),
        is(String.format("%s %s %s?%s", REQUEST_MARKER, REQUEST_METHOD, REQUEST_URI, REQUEST_QUERY_STRING)));

    loggingEvent = resultLog.get(1);
    assertThat(loggingEvent.getLevel(), is(Level.DEBUG));
    assertThat(loggingEvent.getFormattedMessage(), is(String.format("%s %s: %s", REQUEST_MARKER, ENCODING_HEADER, REQUEST_ENCODING)));

    loggingEvent = resultLog.get(2);
    assertThat(loggingEvent.getLevel(), is(Level.DEBUG));
    assertThat(loggingEvent.getFormattedMessage(), is(String.format("%s %s: %s", REQUEST_MARKER, CONTENT_TYPE_HEADER, REQUEST_CONTENT_TYPE)));

    loggingEvent = resultLog.get(3);
    assertThat(loggingEvent.getLevel(), is(Level.INFO));
    assertThat(loggingEvent.getFormattedMessage(), is(String.format("%s %n %s", REQUEST_MARKER, REQUEST_CONTENT)));

    loggingEvent = resultLog.get(4);
    assertThat(loggingEvent.getLevel(), is(Level.INFO));
    assertThat(loggingEvent.getFormattedMessage(), containsString(String.format("%s %d OK", RESPONSE_MARKER, response.getStatus())));

    loggingEvent = resultLog.get(5);
    assertThat(loggingEvent.getLevel(), is(Level.INFO));
    assertThat(loggingEvent.getFormattedMessage(), is(String.format("%s [%d bytes content]", RESPONSE_MARKER, RESPONSE_CONTENT.length())));

    loggingEvent = resultLog.get(6);
    assertThat(loggingEvent.getLevel(), is(Level.WARN));
    assertThat(loggingEvent.getFormattedMessage(), is("Not able to convert content"));
  }

  @Test
  public void shouldNotLogAnythingWhenIsAsyncDispatch() throws ServletException, IOException {
    //given
    LoggingFilter filter = new LoggingFilter() {
      @Override
      protected boolean isAsyncDispatch(HttpServletRequest request) {
        return true;
      }
    };

    //when
    filter.doFilterInternal(wrappedRequest, wrappedResponse, mockFilterChain);

    //then
    verify(mockAppender, never()).doAppend(captorLoggingEvent.capture());
  }

  @Test
  public void shouldNotLogAnythingIfErrorLevelSetForLogger() throws ServletException, IOException {
    //given
    final Logger logger = (Logger) LoggerFactory.getLogger(LoggingFilter.class);
    logger.setLevel(Level.ERROR);

    //when
    filter.doFilterInternal(wrappedRequest, wrappedResponse, mockFilterChain);

    //then
    assertEquals(0, captorLoggingEvent.getAllValues().size());
  }
}