package com.pfm.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfm.Messages;
import com.pfm.category.Category;
import org.junit.Before;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CategoryControllerIntegrationTest {

  private static final String DEFAULT_PATH = "/categories";
  private static final MediaType CONTENT_TYPE = MediaType.APPLICATION_JSON_UTF8;
  private static final Long NOT_EXISTING_ID = 0L;

  private Pattern extractIntFromString = Pattern.compile("([0-9])+");
  private Category testParentCategory = new Category(null, "Food", null);
  private Category testSubCategory = new Category(null, "Snickers", null);
  private Long testParentCategoryId;
  private Long testSubCategoryId;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @Before
  public void defaultGiven() throws Exception {
    testParentCategoryId = addCategory(testParentCategory);
    testParentCategory.setId(testParentCategoryId);
    testSubCategory.setParentCategory(testParentCategory);
    testSubCategoryId = addCategory(testSubCategory);
    testSubCategory.setId(testSubCategoryId);
  }

  @Test
  public void shouldAddCategory() throws Exception {
    //given
    deleteCategoryById(testSubCategoryId);
    deleteCategoryById(testParentCategoryId);
    Category parentCategoryToAdd = new Category(null, "Car", null);
    Category subCategoryToAdd = new Category(null, "Oil", parentCategoryToAdd);
    Category expectedParentCategory = new Category(null, "Car", null);
    Category expectedSubCategory =
        new Category(null, "Oil", new Category(null, "Car", null));

    //when
    long addedParentCategoryId = addCategory(parentCategoryToAdd);
    subCategoryToAdd.getParentCategory().setId(addedParentCategoryId);
    long addedSubCategoryId = addCategory(subCategoryToAdd);

    //then
    expectedParentCategory.setId(addedParentCategoryId);
    expectedSubCategory.setId(addedSubCategoryId);
    expectedSubCategory.getParentCategory().setId(addedParentCategoryId);

    List<Category> categories = getAllCategoriesInDb();

    assertThat(categories.size(), is(2));
    assertThat(categories.get(0), is(equalTo(expectedParentCategory)));
    assertThat(categories.get(1), is(equalTo(expectedSubCategory)));
  }

  @Test
  public void shouldReturnErrorCauseByEmptyNameFiled() throws Exception {
    //given
    Category categoryToAdd = new Category(null, "", null);

    //when
    this.mockMvc
        .perform(post(DEFAULT_PATH).content(json(categoryToAdd)).contentType(CONTENT_TYPE))
        .andExpect(content().string("[\"" + Messages.EMPTY_CATEGORY_NAME + "\"]"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldGetCategories() throws Exception {
    //when
    List<Category> categories = getAllCategoriesInDb();

    //then
    assertThat(categories.size(), is(2));
    assertThat(categories.get(0), is(equalTo(testParentCategory)));
    assertThat(categories.get(1), is(equalTo(testSubCategory)));
  }

  @Test
  public void shouldGetCategoryById() throws Exception {
    //when
    Category resultParentCategory = getCategoryById(testParentCategoryId);
    Category resultSubCategory = getCategoryById(testSubCategoryId);

    //then
    assertThat(resultParentCategory, is(equalTo(testParentCategory)));
    assertThat(resultSubCategory, is(equalTo(testSubCategory)));
  }

  @Test
  public void shouldReturnErrorCausedWrongIdProvidedGetMethod() throws Exception {
    //when
    this.mockMvc.perform(get(DEFAULT_PATH + "/" + testSubCategoryId + 1))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldUpdateCategoryParentCategory() throws Exception {
    //given
    Category categoryToUpdate = testParentCategory;
    categoryToUpdate.setName("Changed Name");

    //when
    this.mockMvc
        .perform(put(DEFAULT_PATH + "/" + testParentCategoryId)
            .content(json(categoryToUpdate)).contentType(CONTENT_TYPE))
        .andExpect(status().isOk());

    //given
    Category result = getCategoryById(testParentCategoryId);
    assertThat(result, is(equalTo(categoryToUpdate)));
  }

  @Test
  public void shouldUpdateSubCategory() throws Exception {
    //given
    Category secondParentCategory = new Category(null, "Second Parent Category", null);
    long secondParentCategoryId = addCategory(secondParentCategory);
    secondParentCategory.setId(secondParentCategoryId);
    Category categoryToUpdate = testSubCategory;
    categoryToUpdate.setName("Changed Name");
    categoryToUpdate.setParentCategory(secondParentCategory);

    //when
    this.mockMvc
        .perform(put(DEFAULT_PATH + "/" + testSubCategoryId)
            .content(json(categoryToUpdate)).contentType(CONTENT_TYPE))
        .andExpect(status().isOk());

    //given
    Category result = getCategoryById(testSubCategoryId);
    assertThat(result, is(equalTo(categoryToUpdate)));
  }

  @Test
  public void shouldReturnErrorCausedWrongIdProvidedUpdateMethod() throws Exception {
    //given
    Category categoryToUpdate = testSubCategory;

    //when
    this.mockMvc
        .perform(put(DEFAULT_PATH + "/" + NOT_EXISTING_ID)
            .content(json(categoryToUpdate)).contentType(CONTENT_TYPE))
        .andExpect(content().string(Messages.UPDATE_CATEGORY_NO_ID_OR_ID_NOT_EXIST))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingParentCategoryIdProviced()
      throws Exception {
    //given
    Category categoryToUpdate = testSubCategory;
    categoryToUpdate
        .setParentCategory(new Category(NOT_EXISTING_ID, "Not existing Category"
            , null));

    //when
    this.mockMvc
        .perform(put(DEFAULT_PATH + "/" + testSubCategoryId)
            .content(json(categoryToUpdate)).contentType(CONTENT_TYPE))
        .andExpect(content().string(
            "[\"" + Messages.PROVIDED_PARRENT_CATEGORY_NOT_EXIST + "\"]"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldDeleteCategory() throws Exception {
    //when
    deleteCategoryById(testSubCategoryId);

    //then
    List<Category> categories = getAllCategoriesInDb();
    assertThat(categories.size(), is(equalTo(1)));
    assertFalse(categories.contains(testSubCategory));
  }

  @Test
  public void shouldDeleteSubCategoryAndThenParentCategory() throws Exception {
    //given

    //when
    deleteCategoryById(testSubCategoryId);
    deleteCategoryById(testParentCategoryId);

    //then
    List<Category> categories = getAllCategoriesInDb();
    assertThat(categories.size(), is(equalTo(0)));
    assertFalse(categories.contains(testSubCategory));
    assertFalse(categories.contains(testParentCategory));
  }

  @Test
  public void shouldReturnErrorCausedByTryingToDeleteParentCategoryOfSubCategory()
      throws Exception {
    //when
    this.mockMvc.perform(delete(DEFAULT_PATH + "/" + testParentCategoryId))
        .andExpect(content().string(Messages.CANNOT_DELETE_PARENT_CATEGORY))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnErrorCausedWrongIdProvidedDeleteMethod() throws Exception {
    //when
    this.mockMvc.perform(delete(DEFAULT_PATH + "/" + NOT_EXISTING_ID))
        .andExpect(status().isNotFound());
  }

  private long addCategory(Category category) throws Exception {
    String response = this.mockMvc
        .perform(post(DEFAULT_PATH).content(json(category)).contentType(CONTENT_TYPE))
        .andExpect(status().isOk()).andReturn()
        .getResponse().getContentAsString();
    return getEntryIdFromServiceResponse(response);
  }

  private Category getCategoryById(long id) throws Exception {
    String response = this.mockMvc.perform(get(DEFAULT_PATH + "/" + id))
        .andExpect(content().contentType(CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return jsonToCategory(response);
  }

  private List<Category> getAllCategoriesInDb() throws Exception {
    String response = this.mockMvc.perform(get(DEFAULT_PATH))
        .andExpect(content().contentType(CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return getCompaniesFromResponse(response);
  }

  private void deleteCategoryById(long id) throws Exception {
    this.mockMvc.perform(delete(DEFAULT_PATH + "/" + id))
        .andExpect(status().isOk());
  }

  private String json(Category category) throws Exception {
    return mapper.writeValueAsString(category);
  }

  private Category jsonToCategory(String jsonCompany) throws Exception {
    return mapper.readValue(jsonCompany, Category.class);
  }

  private List<Category> getCompaniesFromResponse(String response) throws Exception {
    return mapper.readValue(response,
        mapper.getTypeFactory().constructCollectionType(List.class, Category.class));
  }

  private long getEntryIdFromServiceResponse(String response) {
    Matcher matcher = extractIntFromString.matcher(response);
    matcher.find();
    return Long.parseLong(matcher.group(0));
  }
}
