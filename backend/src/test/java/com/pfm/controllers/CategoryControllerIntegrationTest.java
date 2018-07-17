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
import com.pfm.category.CategoryController.CategoryRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CategoryControllerIntegrationTest {

  private static final String DEFAULT_PATH = "/categories";
  private static final MediaType CONTENT_TYPE = MediaType.APPLICATION_JSON_UTF8;
  private static final Long NOT_EXISTING_ID = 0L;

  private Pattern extractIntFromString = Pattern.compile("([0-9])+");
  private CategoryRequest parentCategoryRq = CategoryRequest.builder().name("Food").build();
  private CategoryRequest childCategoryRq = CategoryRequest.builder().name("Snickers").build();
  private Long parentCategoryId;
  private Long childCategoryId;
  private Category parentCategory;
  private Category childCategory;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @Before
  public void defaultGiven() throws Exception {
    parentCategoryId = addCategory(parentCategoryRq);
    childCategoryRq.setParentCategoryId(parentCategoryId);
    childCategoryId = addCategory(childCategoryRq);

    parentCategory = category(parentCategoryRq);
    parentCategory.setId(parentCategoryId);

    childCategory = category(childCategoryRq);
    childCategory.setId(childCategoryId);
    childCategory.getParentCategory().setName(parentCategory.getName());
  }

  @Test
  public void shouldAddCategory() throws Exception {
    //given
    deleteCategoryById(childCategoryId);
    deleteCategoryById(parentCategoryId);
    CategoryRequest parentCategoryToAdd = CategoryRequest.builder().name("Car").build();
    CategoryRequest subCategoryToAdd = CategoryRequest.builder().name("Oil").build();
    Category expectedParentCategory = new Category(null, "Car", null);
    Category expectedSubCategory =
        new Category(null, "Oil", new Category(null, "Car", null));

    //when
    long addedParentCategoryId = addCategory(parentCategoryToAdd);
    subCategoryToAdd.setParentCategoryId(addedParentCategoryId);
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
    CategoryRequest categoryToAdd = CategoryRequest.builder().name("").build();

    //when
    this.mockMvc
        .perform(post(DEFAULT_PATH).content(json(categoryToAdd)).contentType(CONTENT_TYPE))
        .andExpect(content().string("[\"" + Messages.EMPTY_CATEGORY_NAME + "\"]"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnErrorCauseByNameAlreadyExist() throws Exception {
    //given
    CategoryRequest categoryToAdd = CategoryRequest.builder().name(parentCategoryRq.getName())
        .build();

    //when
    this.mockMvc
        .perform(post(DEFAULT_PATH).content(json(categoryToAdd)).contentType(CONTENT_TYPE))
        .andExpect(
            content().string("[\"" + Messages.CATEGORY_WITH_PROVIDED_NAME_ALREADY_EXIST + "\"]"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldGetCategories() throws Exception {
    //when
    List<Category> categories = getAllCategoriesInDb();

    //then
    assertThat(categories.size(), is(2));
    assertThat(categories.get(0), is(equalTo(parentCategory)));
    assertThat(categories.get(1), is(equalTo(childCategory)));
  }

  @Test
  public void shouldGetCategoryById() throws Exception {
    //when
    Category resultParentCategory = getCategoryById(parentCategoryId);
    Category resultSubCategory = getCategoryById(childCategoryId);

    //then
    assertThat(resultParentCategory, is(equalTo(parentCategory)));
    assertThat(resultSubCategory, is(equalTo(childCategory)));
  }

  @Test
  public void shouldReturnErrorCausedWrongIdProvidedGetMethod() throws Exception {
    //when
    this.mockMvc.perform(get(DEFAULT_PATH + "/" + childCategoryId + 1))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldUpdateCategoryParentCategory() throws Exception {
    //given
    CategoryRequest categoryToUpdate = parentCategoryRq; // TODO such assignments does not make sense - maybe you wanted to copy?
    categoryToUpdate.setName("Changed Name");

    Category expectedCategory = category(categoryToUpdate);
    expectedCategory.setId(parentCategoryId);

    //when
    this.mockMvc
        .perform(put(DEFAULT_PATH + "/" + parentCategoryId)
            .content(json(categoryToUpdate)).contentType(CONTENT_TYPE))
        .andExpect(status().isOk());

    //given
    Category result = getCategoryById(parentCategoryId);
    assertThat(result, is(equalTo(expectedCategory)));
  }

  @Test
  public void shouldUpdateSubCategory() throws Exception {
    //given
    CategoryRequest secondParentCategory = CategoryRequest.builder().name("Second Parent Category")
        .build();
    long secondParentCategoryId = addCategory(secondParentCategory);
    CategoryRequest categoryToUpdate = childCategoryRq;
    categoryToUpdate.setName("Changed Name");
    categoryToUpdate.setParentCategoryId(secondParentCategoryId);

    Category expectedCategory = category(categoryToUpdate);
    expectedCategory.setId(childCategoryId);
    expectedCategory.getParentCategory().setName(secondParentCategory.getName());

    //when
    this.mockMvc
        .perform(put(DEFAULT_PATH + "/" + childCategoryId)
            .content(json(categoryToUpdate)).contentType(CONTENT_TYPE))
        .andExpect(status().isOk());

    //given
    Category result = getCategoryById(childCategoryId);
    assertThat(result, is(equalTo(expectedCategory)));
  }

  @Test
  public void shouldReturnErrorCausedWrongIdProvidedUpdateMethod() throws Exception {
    //given
    CategoryRequest categoryToUpdate = childCategoryRq;

    //when
    this.mockMvc
        .perform(put(DEFAULT_PATH + "/" + NOT_EXISTING_ID)
            .content(json(categoryToUpdate)).contentType(CONTENT_TYPE))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingParentCategoryIdProvided()
      throws Exception {
    //given
    CategoryRequest categoryToUpdate = childCategoryRq;
    categoryToUpdate
        .setParentCategoryId(NOT_EXISTING_ID);

    //when
    this.mockMvc
        .perform(put(DEFAULT_PATH + "/" + childCategoryId)
            .content(json(categoryToUpdate)).contentType(CONTENT_TYPE))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(
            "[\"" + Messages.PROVIDED_PARRENT_CATEGORY_NOT_EXIST + "\"]"));
  }

  @Test
  public void shouldReturnErrorCausedByCycling()
      throws Exception {
    //given
    CategoryRequest categoryToUpdate = CategoryRequest.builder().name(parentCategoryRq.getName())
        .parentCategoryId(childCategoryId).build();

    //when
    this.mockMvc
        .perform(put(DEFAULT_PATH + "/" + parentCategoryId)
            .content(json(categoryToUpdate)).contentType(CONTENT_TYPE))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(
            "[\"" + Messages.CATEGORIES_CYCLE_DETECTED + "\"]"));
  }

  @Test
  public void shouldReturnErrorCausedBySettingCategoryToBeSelfParentCategory()
      throws Exception {
    //given
    CategoryRequest categoryToUpdate = CategoryRequest.builder().name(parentCategoryRq.getName())
        .parentCategoryId(parentCategoryId).build();

    //when
    this.mockMvc
        .perform(put(DEFAULT_PATH + "/" + parentCategoryId)
            .content(json(categoryToUpdate)).contentType(CONTENT_TYPE))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(
            "[\"" + Messages.CATEGORIES_CYCLE_DETECTED + "\"]"));
  }

  @Test
  public void shouldDeleteCategory() throws Exception {
    //when
    deleteCategoryById(childCategoryId);

    //then
    List<Category> categories = getAllCategoriesInDb();
    assertThat(categories.size(), is(equalTo(1)));
    assertFalse(categories.contains(childCategoryRq));
  }

  @Test
  public void shouldDeleteSubCategoryAndThenParentCategory() throws Exception {
    //given

    //when
    deleteCategoryById(childCategoryId);
    deleteCategoryById(parentCategoryId);

    //then
    List<Category> categories = getAllCategoriesInDb();
    assertThat(categories.size(), is(equalTo(0)));
    assertFalse(categories.contains(childCategoryRq));
    assertFalse(categories.contains(parentCategoryRq));
  }

  // TODO - tests are in not correct packages
  @Test
  public void shouldReturnErrorCausedByTryingToDeleteParentCategoryOfSubCategory()
      throws Exception {
    //when
    this.mockMvc.perform(delete(DEFAULT_PATH + "/" + parentCategoryId))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(Messages.CANNOT_DELETE_PARENT_CATEGORY));
  }

  @Test
  public void shouldReturnErrorCausedWrongIdProvidedDeleteMethod() throws Exception {
    //when
    this.mockMvc.perform(delete(DEFAULT_PATH + "/" + NOT_EXISTING_ID))
        .andExpect(status().isNotFound());
  }

  private long addCategory(CategoryRequest category) throws Exception {
    String response = this.mockMvc
        .perform(
            post(DEFAULT_PATH)
                .content(json(category))
                .contentType(CONTENT_TYPE))
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
    return getCategoriesFromResponse(response);
  }

  private void deleteCategoryById(long id) throws Exception {
    this.mockMvc.perform(delete(DEFAULT_PATH + "/" + id))
        .andExpect(status().isOk());
  }

  private String json(CategoryRequest category) throws Exception {
    return mapper.writeValueAsString(category);
  }

  private Category jsonToCategory(String jsonCompany) throws Exception {
    return mapper.readValue(jsonCompany, Category.class);
  }

  private List<Category> getCategoriesFromResponse(String response) throws Exception {
    return mapper.readValue(response,
        mapper.getTypeFactory().constructCollectionType(List.class, Category.class));
  }

  private long getEntryIdFromServiceResponse(String response) {
    Matcher matcher = extractIntFromString.matcher(response);
    matcher.find();
    return Long.parseLong(matcher.group(0));
  }

  private Category category(CategoryRequest categoryRequest) {
    Long parentCategoryId = categoryRequest.getParentCategoryId();

    if (parentCategoryId == null) {
      return new Category(null, categoryRequest.getName(), null);
    }

    return new Category(null, categoryRequest.getName(),
        new Category(categoryRequest.getParentCategoryId(), null, null));
  }
}
