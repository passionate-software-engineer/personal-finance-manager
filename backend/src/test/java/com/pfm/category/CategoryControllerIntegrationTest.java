package com.pfm.category;

import static com.pfm.category.CategoryController.convertToCategory;
import static com.pfm.config.MessagesProvider.CANNOT_DELETE_PARENT_CATEGORY;
import static com.pfm.config.MessagesProvider.CATEGORIES_CYCLE_DETECTED;
import static com.pfm.config.MessagesProvider.CATEGORY_WITH_PROVIDED_NAME_ALREADY_EXISTS;
import static com.pfm.config.MessagesProvider.EMPTY_CATEGORY_NAME;
import static com.pfm.config.MessagesProvider.PROVIDED_PARENT_CATEGORY_NOT_EXIST;
import static com.pfm.config.MessagesProvider.getMessage;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.IntegrationTestsBase;
import com.pfm.category.CategoryController.CategoryRequest;
import java.util.List;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

@RunWith(JUnitParamsRunner.class)
public class CategoryControllerIntegrationTest extends IntegrationTestsBase {

  //TODO Rewrite test to use helper class and add Category builder

  //TODO change JunitPArams to Junit5 to avoid CLASS RULE
  @ClassRule
  public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

  @Rule
  public final SpringMethodRule springMethodRule = new SpringMethodRule();

  //TODO remove setup methods where unneeded
  // TODO those global fields is not good idea - each test should initialize data in visible way, if needed wrap that logic into methods and call
  // those methods in // given part of the test
  private final CategoryRequest parentCategoryRq = CategoryRequest.builder().name("Food").build();
  private final CategoryRequest childCategoryRq = CategoryRequest.builder().name("Snickers").build();
  private Long parentCategoryId;
  private Long childCategoryId;
  private Category parentCategory;
  private Category childCategory;

  @Test
  public void shouldAddCategory() throws Exception {
    //given
    setup();
    callRestToDeleteCategoryById(childCategoryId); // TODO that should not be happening - you should start each test from clear state
    callRestToDeleteCategoryById(parentCategoryId);
    CategoryRequest parentCategoryToAdd = CategoryRequest.builder().name("Car").build();
    // TODO move all that logic to TestCategoryProvider - tests will be cleaner
    CategoryRequest subCategoryToAdd = CategoryRequest.builder().name("Oil").build();
    Category expectedParentCategory = Category.builder().name("Car").build();
    Category expectedSubCategory = Category.builder().name("Oil").parentCategory(expectedParentCategory).build();

    //when
    long addedParentCategoryId = callRestToaddCategoryAndReturnId(parentCategoryToAdd);
    subCategoryToAdd.setParentCategoryId(addedParentCategoryId);
    // TODO that can be hidden in callRestToaddCategoryAndReturnId method, just pass id of parent category to it, don't set it before
    long addedSubCategoryId = callRestToaddCategoryAndReturnId(subCategoryToAdd);

    //then
    expectedParentCategory.setId(addedParentCategoryId);
    expectedSubCategory.setId(addedSubCategoryId);
    expectedSubCategory.getParentCategory().setId(addedParentCategoryId);

    List<Category> categories = callRestToGetAllCategories();

    assertThat(categories.size(), is(2));
    assertThat(categories.get(0), is(equalTo(expectedParentCategory)));
    assertThat(categories.get(1), is(equalTo(expectedSubCategory)));
  }

  @Test
  @Parameters(method = "emptyAccountNameParameters")
  public void shouldReturnErrorCauseByEmptyNameFiled(String name) throws Exception {
    //given
    setup();
    CategoryRequest categoryToAdd = CategoryRequest.builder().name(name).build();

    //when
    mockMvc
        .perform(
            post(CATEGORIES_SERVICE_PATH)
                .content(json(categoryToAdd))
                .contentType(JSON_CONTENT_TYPE)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", is(getMessage(EMPTY_CATEGORY_NAME))));
  }

  @SuppressWarnings("unused")
  private Object[] emptyAccountNameParameters() {
    return new Object[]{"", " ", "    ", null};
  }

  @Test
  public void shouldReturnErrorCausedByNameAlreadyExist() throws Exception {
    //given
    setup();
    CategoryRequest categoryToAdd = CategoryRequest.builder().name(parentCategoryRq.getName())
        .build();

    //when
    mockMvc
        .perform(
            post(CATEGORIES_SERVICE_PATH)
                .content(json(categoryToAdd))
                .contentType(JSON_CONTENT_TYPE)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", is(getMessage(CATEGORY_WITH_PROVIDED_NAME_ALREADY_EXISTS))));
  }

  @Test
  public void shouldGetCategories() throws Exception {
    //when
    setup();
    List<Category> categories = callRestToGetAllCategories();

    //then
    assertThat(categories.size(), is(2));
    assertThat(categories.get(0), is(equalTo(parentCategory)));
    assertThat(categories.get(1), is(equalTo(childCategory)));
  }

  @Test
  public void shouldGetCategoryById() throws Exception {
    //when // TODO that should be 2 separate tests
    setup();
    Category resultParentCategory = callRestToGetCategoryById(parentCategoryId);
    Category resultSubCategory = callRestToGetCategoryById(childCategoryId);

    //then
    assertThat(resultParentCategory, is(equalTo(parentCategory)));
    assertThat(resultSubCategory, is(equalTo(childCategory)));
  }

  @Test
  public void shouldReturnErrorCausedWrongIdProvidedGetMethod() throws Exception {
    //given
    setup();

    //when
    mockMvc
        .perform(get(CATEGORIES_SERVICE_PATH + "/" + childCategoryId + 1))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldUpdateCategoryParentCategory() throws Exception {
    //given
    setup();
    CategoryRequest categoryToUpdate = parentCategoryRq; // TODO such assignments does not make sense - maybe you wanted to copy?
    categoryToUpdate.setName("Changed Name"); // Please rethink how you handle objects - TestCategoryProvider will help you a lot.

    Category expectedCategory = convertToCategory(categoryToUpdate);
    expectedCategory.setId(parentCategoryId);

    //when
    mockMvc
        .perform(put(CATEGORIES_SERVICE_PATH + "/" + parentCategoryId)
            .content(json(categoryToUpdate))
            .contentType(JSON_CONTENT_TYPE)
        )
        .andExpect(status().isOk());

    //given
    Category result = callRestToGetCategoryById(parentCategoryId);
    assertThat(result, is(equalTo(expectedCategory)));
  }

  @Test
  public void shouldUpdateSubCategory() throws Exception {
    //given
    setup();
    CategoryRequest secondParentCategory = CategoryRequest.builder()
        .name("Second Parent Category")
        .build();

    long secondParentCategoryId = callRestToaddCategoryAndReturnId(secondParentCategory);
    CategoryRequest categoryToUpdate = childCategoryRq;
    categoryToUpdate.setName("Changed Name");
    categoryToUpdate.setParentCategoryId(secondParentCategoryId);

    Category expectedCategory = convertToCategory(categoryToUpdate);
    expectedCategory.setId(childCategoryId);
    expectedCategory.getParentCategory().setName(secondParentCategory.getName());

    //when
    mockMvc
        .perform(put(CATEGORIES_SERVICE_PATH + "/" + childCategoryId)
            .content(json(categoryToUpdate)).contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk());

    //given
    Category result = callRestToGetCategoryById(childCategoryId);
    assertThat(result, is(equalTo(expectedCategory)));
  }

  @Test
  public void shouldReturnErrorCausedWrongIdProvidedUpdateMethod() throws Exception {
    //given
    setup();

    //when
    mockMvc
        .perform(put(CATEGORIES_SERVICE_PATH + "/" + NOT_EXISTING_ID)
            .content(json(childCategoryRq)).contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingParentCategoryIdProvided()
      throws Exception {
    //given
    setup();
    CategoryRequest categoryToUpdate = childCategoryRq;
    categoryToUpdate
        .setParentCategoryId(NOT_EXISTING_ID);

    //when
    mockMvc
        .perform(put(CATEGORIES_SERVICE_PATH + "/" + childCategoryId)
            .content(json(categoryToUpdate)).contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", is(getMessage(PROVIDED_PARENT_CATEGORY_NOT_EXIST))));
  }

  @Test
  public void shouldReturnErrorCausedByCycling()
      throws Exception {
    //given
    setup();
    CategoryRequest categoryToUpdate = CategoryRequest.builder().name(parentCategoryRq.getName())
        .parentCategoryId(childCategoryId).build();

    //when
    performUpdateRequestAndAssertCycleErrorIsReturned(categoryToUpdate);
  }

  @Test
  public void shouldReturnErrorCausedBySettingCategoryToBeSelfParentCategory()
      throws Exception {
    //given
    setup();
    CategoryRequest categoryToUpdate = CategoryRequest.builder().name(parentCategoryRq.getName())
        .parentCategoryId(parentCategoryId).build();

    //when
    performUpdateRequestAndAssertCycleErrorIsReturned(categoryToUpdate);
  }

  private void performUpdateRequestAndAssertCycleErrorIsReturned(CategoryRequest categoryToUpdate) throws Exception {
    //when
    mockMvc
        .perform(put(CATEGORIES_SERVICE_PATH + "/" + parentCategoryId)
            .content(json(categoryToUpdate)).contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", is(getMessage(CATEGORIES_CYCLE_DETECTED))));
  }

  @Test
  public void shouldDeleteCategory() throws Exception {
    //given
    setup();
    //when
    callRestToDeleteCategoryById(childCategoryId);

    //then
    List<Category> categories = callRestToGetAllCategories();
    assertThat(categories.size(), is(equalTo(1)));
    assertFalse(
        categories.contains(childCategoryRq)); // TODO it will always be false as types don't match
  }

  @Test
  public void shouldDeleteSubCategoryAndThenParentCategory() throws Exception {
    //given
    setup();
    //when
    callRestToDeleteCategoryById(childCategoryId);
    callRestToDeleteCategoryById(parentCategoryId);

    //then
    List<Category> categories = callRestToGetAllCategories();
    assertThat(categories.size(), is(equalTo(0))); // TODO it will always be false as types don't match
    assertFalse(categories.contains(childCategoryRq));
    // TODO http://hamcrest.org/JavaHamcrest/javadoc/1.3/org/hamcrest/core/IsCollectionContaining.html
    assertFalse(categories.contains(parentCategoryRq)); // TODO it will always be false as types don't match
  }

  @Test
  public void shouldReturnErrorCausedByTryingToDeleteParentCategoryOfSubCategory()
      throws Exception {
    //given
    setup();
    //when
    mockMvc.perform(delete(CATEGORIES_SERVICE_PATH + "/" + parentCategoryId))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(getMessage(CANNOT_DELETE_PARENT_CATEGORY)));
  }

  @Test
  public void shouldReturnErrorCausedWrongIdProvidedDeleteMethod() throws Exception {
    //when
    setup();
    //when
    mockMvc.perform(delete(CATEGORIES_SERVICE_PATH + "/" + NOT_EXISTING_ID))
        .andExpect(status().isNotFound());
  }

  private void setup() throws Exception {
    parentCategoryId = callRestToaddCategoryAndReturnId(parentCategoryRq);
    childCategoryRq.setParentCategoryId(parentCategoryId);
    childCategoryId = callRestToaddCategoryAndReturnId(childCategoryRq);

    parentCategory = convertToCategory(parentCategoryRq);
    parentCategory.setId(parentCategoryId);

    childCategory = convertToCategory(childCategoryRq);
    childCategory.setId(childCategoryId);
    childCategory.getParentCategory().setName(parentCategory.getName());
  }

}
