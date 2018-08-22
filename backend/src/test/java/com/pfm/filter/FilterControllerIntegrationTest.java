package com.pfm.filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfm.account.Account;
import com.pfm.category.Category;
import java.util.List;
import org.flywaydb.core.Flyway;
import org.junit.Before;
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
public class FilterControllerIntegrationTest {

  private static final String ACCOUNTS_SERVICE_PATH = "/accounts";
  private static final String FILTERS_SERVICE_PATH = "/filters";
  private static final String CATEGORIES_SERVICE_PATH = "/categories";
  private static final MediaType JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON_UTF8;
  private static final long NOT_EXISTING_ID = 0;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private Flyway flyway;

  @Before
  public void before() {
    flyway.clean();
    flyway.migrate();
  }

  private long callRestServiceToAddAccountAndReturnId(Account account) throws Exception {
    String response =
        mockMvc
            .perform(post(ACCOUNTS_SERVICE_PATH)
                .content(json(account))
                .contentType(JSON_CONTENT_TYPE))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  private long callRestServiceToAddFilterAndReturnId(FilterRequest filterRequest, List<Long> accountIds, List<Long> categoriesIds) throws Exception {
    filterRequest.setAccountsIds(accountIds);
    filterRequest.setCategoryIds(categoriesIds);
    String response =
        mockMvc
            .perform(post(FILTERS_SERVICE_PATH)
                .content(json(filterRequest))
                .contentType(JSON_CONTENT_TYPE))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  private long callRestServiceToAddCategoryAndReturnId(Category category) throws Exception {
    String response =
        mockMvc
            .perform(post(CATEGORIES_SERVICE_PATH)
                .content(json(category))
                .contentType(JSON_CONTENT_TYPE))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  private Filter convertFilterRequestToFilterAndSetId(long filterId, FilterRequest filterRequest) {
    return Filter.builder()
        .id(filterId)
        .name(filterRequest.getName())
        .dateFrom(filterRequest.getDateFrom())
        .dateTo(filterRequest.getDateTo())
        .accountsIds(filterRequest.getAccountsIds())
        .categoriesIds(filterRequest.getCategoryIds())
        .priceFrom(filterRequest.getPriceFrom())
        .priceTo(filterRequest.getPriceTo())
        .description(filterRequest.getDescription())
        .build();
  }

  private Filter getFilterById(long id) throws Exception {
    String response = mockMvc.perform(get(FILTERS_SERVICE_PATH + "/" + id))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return jsonToFilter(response);
  }

  private void deleteFilterById(long id) throws Exception {
    mockMvc.perform(delete(FILTERS_SERVICE_PATH + "/" + id))
        .andExpect(status().isOk());
  }

  private List<Filter> getAllFiltersFromDatabase() throws Exception {
    String response = mockMvc.perform(get(FILTERS_SERVICE_PATH))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return getFiltersFromResponse(response);
  }

  private String json(Object object) throws Exception {
    return mapper.writeValueAsString(object);
  }

  private Account jsonToAccount(String jsonAccount) throws Exception {
    return mapper.readValue(jsonAccount, Account.class);
  }

  private Filter jsonToFilter(String jsonFilter) throws Exception {
    return mapper.readValue(jsonFilter, Filter.class);
  }

  private List<Filter> getFiltersFromResponse(String response) throws Exception {
    return mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, Filter.class));
  }
}

