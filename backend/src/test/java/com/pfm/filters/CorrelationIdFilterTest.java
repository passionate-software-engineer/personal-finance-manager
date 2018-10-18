package com.pfm.filters;

import static com.pfm.filters.CorrelationIdFilter.CORRELATION_ID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CorrelationIdFilterTest {

  private static final String INVOICES_SERVICE_PATH = "/accounts";

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void shouldSetRandomUuidAsCorrelationIdIfNotProvidedInRequest() throws Exception {
    // given

    // when
    mockMvc
        .perform(get(INVOICES_SERVICE_PATH))
        .andExpect(content().contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isUnauthorized());

    // then
    // assertThat(MDC.get(CORRELATION_ID), is(not(nullValue())));
    // TODO add assertion on logger: https://codingcraftsman.wordpress.com/2015/04/28/log4j2-mocking-with-mockito-and-junit/
  }

  @Test
  public void shouldUseProvidedCorrelationIdIfProvidedInRequest() throws Exception {
    // given

    // when
    mockMvc
        .perform(
            get(INVOICES_SERVICE_PATH)
                .header(CORRELATION_ID, UUID.randomUUID().toString())
        )
        .andExpect(content().contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isUnauthorized());

    // then
    // assertThat(MDC.get(CORRELATION_ID), is(not(nullValue())));
    // TODO add assertion on logger: https://codingcraftsman.wordpress.com/2015/04/28/log4j2-mocking-with-mockito-and-junit/
  }

}