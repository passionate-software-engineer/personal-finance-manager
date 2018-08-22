package com.pfm;

import static com.pfm.category.CategoryController.convertToCategory;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfm.account.Account;
import com.pfm.category.Category;
import com.pfm.category.CategoryController.CategoryRequest;
import java.util.List;
import org.flywaydb.core.Flyway;
import org.junit.Before;
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
public abstract class IntegrationTestsBase {

  protected static final String INVOICES_SERVICE_PATH = "/accounts";
  protected static final MediaType JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON_UTF8;
  protected static final long NOT_EXISTING_ID = 0;
  protected static final String CATEGORIES_SERVICE_PATH = "/categories";


  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper mapper;

  @Autowired
  protected Flyway flyway;

  @Before
  public void before() {
    flyway.clean();
    flyway.migrate();
  }

  protected String json(Object object) throws Exception {
    return mapper.writeValueAsString(object);
  }

  protected long callRestServiceToAddAccountAndReturnId(Account account) throws Exception {
    String response =
        mockMvc
            .perform(post(INVOICES_SERVICE_PATH)
                .content(json(account))
                .contentType(JSON_CONTENT_TYPE))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  protected long addCategoryAndReturnId(CategoryRequest category) throws Exception {
    String response = mockMvc
        .perform(
            post(CATEGORIES_SERVICE_PATH)
                .content(json(category))
                .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk()).andReturn()
        .getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  protected Category getCategoryById(long id) throws Exception {
    String response = mockMvc.perform(get(CATEGORIES_SERVICE_PATH + "/" + id))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return jsonToCategory(response);
  }

  protected List<Category> getAllCategoriesFromDatabase() throws Exception {
    String response = mockMvc.perform(get(CATEGORIES_SERVICE_PATH))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return getCategoriesFromResponse(response);
  }

  protected void deleteCategoryById(long id) throws Exception {
    mockMvc.perform(delete(CATEGORIES_SERVICE_PATH + "/" + id))
        .andExpect(status().isOk());
  }

  protected Category jsonToCategory(String jsonCategory) throws Exception {
    return mapper.readValue(jsonCategory, Category.class);
  }

  protected List<Category> getCategoriesFromResponse(String response) throws Exception {
    return mapper.readValue(response,
        mapper.getTypeFactory().constructCollectionType(List.class, Category.class));
  }



}
