package com.pfm.category;

import static com.pfm.category.CategoryController.convertToCategory;
import static com.pfm.config.MessagesProvider.CANNOT_DELETE_PARENT_CATEGORY;
import static com.pfm.config.MessagesProvider.CATEGORIES_CYCLE_DETECTED;
import static com.pfm.config.MessagesProvider.CATEGORY_WITH_PROVIDED_NAME_ALREADY_EXIST;
import static com.pfm.config.MessagesProvider.EMPTY_CATEGORY_NAME;
import static com.pfm.config.MessagesProvider.PROVIDED_PARENT_CATEGORY_NOT_EXIST;
import static com.pfm.config.MessagesProvider.getMessage;
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
import com.pfm.category.CategoryController.CategoryRequest;
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
public class CategoryControllerIntegrationTest {

  private static final String CATEGORIES_SERVICE_PATH = "/categories";
  private static final MediaType CONTENT_TYPE = MediaType.APPLICATION_JSON_UTF8;
  private static final Long NOT_EXISTING_ID = 0L;

  // TODO those global fields is not good idea - each test should initialize data in visible way, if needed wrap that logic into methods and call
  // those methods in // given part of the test
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

  @Autowired
  private Flyway flyway;

  @Before
  public void before() throws Exception {
    flyway.clean();
    flyway.migrate();

    // TODO those global fields is not good idea - each test should initialize data in visible way, if needed wrap that logic into methods and call
    // those methods in // given part of the test
    parentCategoryId = addCategory(parentCategoryRq);
    childCategoryRq.setParentCategoryId(parentCategoryId);
    childCategoryId = addCategory(childCategoryRq);

    parentCategory = convertToCategory(parentCategoryRq);
    parentCategory.setId(parentCategoryId);

    childCategory = convertToCategory(childCategoryRq);
    childCategory.setId(childCategoryId);
    childCategory.getParentCategory().setName(parentCategory.getName());
  }

  @Test
  public void shouldAddCategory() throws Exception {
    //given
    deleteCategoryById(
        childCategoryId); // TODO that should not be happening - you should start each test from clear state
    deleteCategoryById(parentCategoryId);
    CategoryRequest parentCategoryToAdd = CategoryRequest.builder().name("Car")
        .build(); // TODO move all that logic to TestCategoryProvider - tests will be cleaner
    CategoryRequest subCategoryToAdd = CategoryRequest.builder().name("Oil").build();
    Category expectedParentCategory = new Category(null, "Car", null);
    Category expectedSubCategory =
        new Category(null, "Oil", new Category(null, "Car", null));

    //when
    long addedParentCategoryId = addCategory(parentCategoryToAdd);
    subCategoryToAdd.setParentCategoryId(
        addedParentCategoryId); // TODO that can be hidden in addCategory method, just pass id of parent category to it, don't set it before
    long addedSubCategoryId = addCategory(subCategoryToAdd);

    //then
    expectedParentCategory.setId(addedParentCategoryId);
    expectedSubCategory.setId(addedSubCategoryId);
    expectedSubCategory.getParentCategory().setId(addedParentCategoryId);

    List<Category> categories = getAllCategoriesFromDatabase();

    assertThat(categories.size(), is(2));
    assertThat(categories.get(0), is(equalTo(expectedParentCategory)));
    assertThat(categories.get(1), is(equalTo(expectedSubCategory)));
  }

  @Test
  public void shouldReturnErrorCauseByEmptyNameFiled() throws Exception {
    //given
    CategoryRequest categoryToAdd = CategoryRequest.builder().name(null).build();

    //when
    this.mockMvc
        .perform(
            post(CATEGORIES_SERVICE_PATH).content(json(categoryToAdd)).contentType(CONTENT_TYPE))
        .andExpect(content().string("[\"" + getMessage(EMPTY_CATEGORY_NAME)
            + "\"]")) // TODO if you get list as a result then first assert size and then take element from index - [ ] is making code less readable
        .andExpect(status()
            .isBadRequest()); // TODO always assert status first - further assertions does not make sense if status is not correct
  }

  @Test
  public void shouldReturnErrorCausedByNameAlreadyExist() throws Exception {
    //given
    CategoryRequest categoryToAdd = CategoryRequest.builder()
        .name(parentCategoryRq.getName())
        .build();

    //when
    this.mockMvc
        .perform(
            post(CATEGORIES_SERVICE_PATH).content(json(categoryToAdd)).contentType(CONTENT_TYPE))
        .andExpect(
            content().string("[\"" + getMessage(CATEGORY_WITH_PROVIDED_NAME_ALREADY_EXIST) + "\"]"))
        .andExpect(status()
            .isBadRequest()); // TODO always assert status first - further assertions does not make sense if status is not correct
  }

  @Test
  public void shouldGetCategories() throws Exception {
    //when
    List<Category> categories = getAllCategoriesFromDatabase();

    //then
    assertThat(categories.size(), is(2));
    assertThat(categories.get(0), is(equalTo(parentCategory)));
    assertThat(categories.get(1), is(equalTo(childCategory)));
  }

  @Test
  public void shouldGetCategoryById() throws Exception {
    //when // TODO that should be 2 separate tests
    Category resultParentCategory = getCategoryById(parentCategoryId);
    Category resultSubCategory = getCategoryById(childCategoryId);

    //then
    assertThat(resultParentCategory, is(equalTo(parentCategory)));
    assertThat(resultSubCategory, is(equalTo(childCategory)));
  }

  @Test
  public void shouldReturnErrorCausedWrongIdProvidedGetMethod() throws Exception {
    //when
    this.mockMvc.perform(get(CATEGORIES_SERVICE_PATH + "/" + childCategoryId + 1))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldUpdateCategoryParentCategory() throws Exception {
    //given
    CategoryRequest categoryToUpdate = parentCategoryRq; // TODO such assignments does not make sense - maybe you wanted to copy?
    categoryToUpdate.setName("Changed Name"); // Please rethink how you handle objects - TestCategoryProvider will help you a lot.

    Category expectedCategory = convertToCategory(categoryToUpdate);
    expectedCategory.setId(parentCategoryId);

    //when
    this.mockMvc
        .perform(put(CATEGORIES_SERVICE_PATH + "/" + parentCategoryId)
            .content(json(categoryToUpdate)).contentType(CONTENT_TYPE))
        .andExpect(status().isOk());

    //given
    Category result = getCategoryById(parentCategoryId);
    assertThat(result, is(equalTo(expectedCategory)));
  }

  @Test
  public void shouldUpdateSubCategory() throws Exception {
    //given
    CategoryRequest secondParentCategory = CategoryRequest.builder()
        .name("Second Parent Category")
        .build();

    long secondParentCategoryId = addCategory(secondParentCategory);
    CategoryRequest categoryToUpdate = childCategoryRq;
    categoryToUpdate.setName("Changed Name");
    categoryToUpdate.setParentCategoryId(secondParentCategoryId);

    Category expectedCategory = convertToCategory(categoryToUpdate);
    expectedCategory.setId(childCategoryId);
    expectedCategory.getParentCategory().setName(secondParentCategory.getName());

    //when
    this.mockMvc
        .perform(put(CATEGORIES_SERVICE_PATH + "/" + childCategoryId)
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
        .perform(put(CATEGORIES_SERVICE_PATH + "/" + NOT_EXISTING_ID)
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
        .perform(put(CATEGORIES_SERVICE_PATH + "/" + childCategoryId)
            .content(json(categoryToUpdate)).contentType(CONTENT_TYPE))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(
            "[\"" + getMessage(PROVIDED_PARENT_CATEGORY_NOT_EXIST) + "\"]"));
  }

  @Test
  public void shouldReturnErrorCausedByCycling()
      throws Exception {
    //given
    CategoryRequest categoryToUpdate = CategoryRequest.builder().name(parentCategoryRq.getName())
        .parentCategoryId(childCategoryId).build();

    //when
    performUpdateRequestAndAssertCycleErrorIsReturned(categoryToUpdate);
  }

  @Test
  public void shouldReturnErrorCausedBySettingCategoryToBeSelfParentCategory()
      throws Exception {
    //given
    CategoryRequest categoryToUpdate = CategoryRequest.builder().name(parentCategoryRq.getName())
        .parentCategoryId(parentCategoryId).build();

    //when
    performUpdateRequestAndAssertCycleErrorIsReturned(categoryToUpdate);
  }

  private void performUpdateRequestAndAssertCycleErrorIsReturned(CategoryRequest categoryToUpdate) throws Exception {
    //when
    this.mockMvc
        .perform(put(CATEGORIES_SERVICE_PATH + "/" + parentCategoryId)
            .content(json(categoryToUpdate)).contentType(CONTENT_TYPE))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(
            "[\"" + getMessage(CATEGORIES_CYCLE_DETECTED) + "\"]"));
  }

  @Test
  public void shouldDeleteCategory() throws Exception {
    //when
    deleteCategoryById(childCategoryId);

    //then
    List<Category> categories = getAllCategoriesFromDatabase();
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
    List<Category> categories = getAllCategoriesFromDatabase();
    assertThat(categories.size(), is(equalTo(0)));
    assertFalse(categories.contains(
        childCategoryRq)); // TODo http://hamcrest.org/JavaHamcrest/javadoc/1.3/org/hamcrest/core/IsCollectionContaining.html
    assertFalse(categories.contains(parentCategoryRq));
  }

  @Test
  public void shouldReturnErrorCausedByTryingToDeleteParentCategoryOfSubCategory()
      throws Exception {
    //given

    //when
    this.mockMvc.perform(delete(CATEGORIES_SERVICE_PATH + "/" + parentCategoryId))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(getMessage(CANNOT_DELETE_PARENT_CATEGORY)));
  }

  @Test
  public void shouldReturnErrorCausedWrongIdProvidedDeleteMethod() throws Exception {
    //when
    this.mockMvc.perform(delete(CATEGORIES_SERVICE_PATH + "/" + NOT_EXISTING_ID))
        .andExpect(status().isNotFound());
  }

  private long addCategory(CategoryRequest category) throws Exception {
    String response = this.mockMvc
        .perform(
            post(CATEGORIES_SERVICE_PATH)
                .content(json(category))
                .contentType(CONTENT_TYPE))
        .andExpect(status().isOk()).andReturn()
        .getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  private Category getCategoryById(long id) throws Exception {
    String response = this.mockMvc.perform(get(CATEGORIES_SERVICE_PATH + "/" + id))
        .andExpect(content().contentType(CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return jsonToCategory(response);
  }

  private List<Category> getAllCategoriesFromDatabase() throws Exception {
    String response = this.mockMvc.perform(get(CATEGORIES_SERVICE_PATH))
        .andExpect(content().contentType(CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return getCategoriesFromResponse(response);
  }

  private void deleteCategoryById(long id) throws Exception {
    this.mockMvc.perform(delete(CATEGORIES_SERVICE_PATH + "/" + id))
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

}
