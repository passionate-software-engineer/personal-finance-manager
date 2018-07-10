package com.pfm.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.ObjectMapperHelper;
import com.pfm.category.Category;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CategoryControllerIntegrationTest {

  private static final String DEFAULT_PATH = "/categories";
  private static final MediaType CONTENT_TYPE = MediaType.APPLICATION_JSON_UTF8;

  private ObjectMapperHelper<Category> objectMapperHelper = new ObjectMapperHelper<>(
      Category.class);

  private Category testCategory = new Category(null, "Food", null);
  private Category testCategoryWithParentCategory
      = new Category(null, "Snickers", null);


  @Autowired
  private MockMvc mockMvc;


  @Test
  public void shouldAddCompany() throws Exception {
    //given
    Category expectedCategory = testCompany;
    //when
    String respone = this.mockMvc
        .perform(post(DEFAULT_PATH).content(json(testCompany)).contentType(CONTENT_TYPE))
        .andExpect(handler().methodName(ADD_COMPANY_METHOD)).andExpect(status().isOk()).andReturn()
        .getResponse().getContentAsString();
    //then
    expectedCompany.setId(getEntryIdFromServiceResponse(respone));

    String response = this.mockMvc.perform(get(DEFAULT_PATH))
        .andExpect(content().contentType(CONTENT_TYPE))
        .andExpect(handler().methodName(GET_COMPANIES_BY_DATE)).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    List<Company> companies = getCompaniesFromResponse(response);
    assertThat(companies.size(), is(1));
    assertThat(companies.get(0), is(equalTo(testCompany)));
  }

}
