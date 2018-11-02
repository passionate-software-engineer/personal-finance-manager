package com.pfm.category;

import static com.pfm.config.MessagesProvider.CANNOT_DELETE_PARENT_CATEGORY;
import static com.pfm.config.MessagesProvider.CATEGORIES_CYCLE_DETECTED;
import static com.pfm.config.MessagesProvider.CATEGORY_WITH_PROVIDED_NAME_ALREADY_EXISTS;
import static com.pfm.config.MessagesProvider.EMPTY_CATEGORY_NAME;
import static com.pfm.config.MessagesProvider.PROVIDED_PARENT_CATEGORY_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.getMessage;
import static com.pfm.helpers.TestCategoryProvider.categoryCar;
import static com.pfm.helpers.TestCategoryProvider.categoryFood;
import static com.pfm.helpers.TestCategoryProvider.categoryHome;
import static com.pfm.helpers.TestCategoryProvider.categoryOil;
import static com.pfm.helpers.TestUsersProvider.userMarian;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.helpers.IntegrationTestsBase;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;

public class CategoryControllerIntegrationTest extends IntegrationTestsBase {

  @BeforeEach
  public void setup() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());
  }

  @Test
  public void shouldAddCategory() throws Exception {

    //when
    Category categoryCar = categoryCar();
    long carCategoryId = callRestToAddCategoryAndReturnId(categoryCar, token);
    Category categoryOil = categoryOil();
    long oilCategoryId = callRestToAddCategoryWithSpecifiedParentCategoryIdAndReturnId(categoryOil, carCategoryId, token);

    //then
    categoryCar.setId(carCategoryId);

    categoryOil.setId(oilCategoryId);
    categoryOil.setParentCategory(categoryCar);

    List<Category> categories = callRestToGetAllCategories(token);

    assertThat(categories.size(), is(2));
    assertThat(categories, containsInAnyOrder(categoryCar, categoryOil));
  }

  @ParameterizedTest
  @MethodSource("emptyAccountNameParameters")
  public void shouldReturnErrorCauseByEmptyNameFiled(String name) throws Exception {

    //given
    CategoryRequest categoryToAdd = CategoryRequest.builder().name(name).build();

    //when
    mockMvc
        .perform(
            post(CATEGORIES_SERVICE_PATH)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(json(categoryToAdd))
                .contentType(JSON_CONTENT_TYPE)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", is(getMessage(EMPTY_CATEGORY_NAME))));
  }

  private static Object[] emptyAccountNameParameters() {
    return new Object[]{"", " ", "    ", null};
  }

  @Test
  public void shouldReturnErrorCausedByNameAlreadyExist() throws Exception {

    //given
    callRestToAddCategoryAndReturnId(categoryCar(), token);
    CategoryRequest categoryToAdd = CategoryRequest.builder().name(categoryCar().getName())
        .build();

    //when
    mockMvc
        .perform(
            post(CATEGORIES_SERVICE_PATH)
                .header(HttpHeaders.AUTHORIZATION, token)
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
    Category categoryCar = categoryCar();
    long categoryCarId = callRestToAddCategoryAndReturnId(categoryCar, token);

    Category categoryHome = categoryHome();
    long categoryHomeId = callRestToAddCategoryAndReturnId(categoryHome, token);

    final List<Category> categories = callRestToGetAllCategories(token);

    //then
    categoryCar.setId(categoryCarId);

    categoryHome.setId(categoryHomeId);

    assertThat(categories.size(), is(2));
    assertThat(categories, containsInAnyOrder(categoryCar, categoryHome));
  }

  @Test
  public void shouldGetCategoryById() throws Exception {

    //given
    Category categoryCar = categoryCar();
    long categoryCarId = callRestToAddCategoryAndReturnId(categoryCar, token);

    //when
    Category actualCarCategory = callRestToGetCategoryById(categoryCarId, token);

    // then
    categoryCar.setId(categoryCarId);
    assertThat(actualCarCategory, is(equalTo(categoryCar)));
  }

  @Test
  public void shouldReturnErrorCausedWrongIdProvidedGetMethod() throws Exception {

    //when
    mockMvc
        .perform(get(CATEGORIES_SERVICE_PATH + "/" + NOT_EXISTING_ID)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldUpdateCategory() throws Exception {

    //given
    long homeCategoryId = callRestToAddCategoryAndReturnId(categoryHome(), token);
    Category categoryToUpdate = categoryHome();
    categoryToUpdate.setName("Second Home");

    //when
    callRestToUpdateCategory(homeCategoryId, categoryToCategoryRequest(categoryToUpdate), token);

    //then
    categoryToUpdate.setId(homeCategoryId);

    Category result = callRestToGetCategoryById(homeCategoryId, token);
    assertThat(result, is(equalTo(categoryToUpdate)));
  }

  @Test
  public void shouldUpdateSubCategory() throws Exception {

    //given
    long categoryCarId = callRestToAddCategoryAndReturnId(categoryCar(), token);
    long categoryOilId = callRestToAddCategoryWithSpecifiedParentCategoryIdAndReturnId(categoryOil(), categoryCarId, token);
    CategoryRequest categoryOilToUpdate = CategoryRequest.builder()
        .name("Mannol Oil")
        .build();

    //when
    callRestToUpdateCategory(categoryOilId, categoryOilToUpdate, token);

    //given
    Category result = callRestToGetCategoryById(categoryOilId, token);

    final Category expected = convertCategoryRequestToCategoryAndSetId(categoryOilId, userId, categoryOilToUpdate);

    assertThat(result, is(equalTo(expected)));
  }

  @Test
  public void shouldReturnErrorCausedWrongIdProvidedUpdateMethod() throws Exception {

    //when
    mockMvc
        .perform(put(CATEGORIES_SERVICE_PATH + "/" + NOT_EXISTING_ID)
            .header(HttpHeaders.AUTHORIZATION, token)
            .content(json(categoryToCategoryRequest(categoryOil()))).contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingParentCategoryIdProvided() throws Exception {

    //given
    long categoryId = callRestToAddCategoryAndReturnId(categoryCar(), token);
    CategoryRequest categoryToUpdate = categoryToCategoryRequest(categoryCar());
    categoryToUpdate.setParentCategoryId(NOT_EXISTING_ID);

    //when
    mockMvc
        .perform(put(CATEGORIES_SERVICE_PATH + "/" + categoryId)
            .header(HttpHeaders.AUTHORIZATION, token)
            .content(json(categoryToUpdate)).contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", is(getMessage(PROVIDED_PARENT_CATEGORY_DOES_NOT_EXIST))));
  }

  @Test
  public void shouldReturnTrueWhenNoCycleExistsAnd2CategoriesWereUsed() throws Exception {

    // given
    long foodCategoryId = callRestToAddCategoryAndReturnId(categoryFood(), token);
    long carCategoryId = callRestToAddCategoryAndReturnId(categoryCar(), token);

    CategoryRequest newCategoryContent = categoryToCategoryRequest(categoryCar());
    newCategoryContent.setParentCategoryId(foodCategoryId);

    // when // TODO require space after comment start (checkstyle) :)
    mockMvc
        .perform(put(CATEGORIES_SERVICE_PATH + "/" + carCategoryId)
            .header(HttpHeaders.AUTHORIZATION, token)
            .content(json(newCategoryContent)).contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldReturnErrorCausedByCycling() throws Exception {

    //given
    long carCategoryId = callRestToAddCategoryAndReturnId(categoryCar(), token);
    long oilCategoryId = callRestToAddCategoryWithSpecifiedParentCategoryIdAndReturnId(categoryOil(), carCategoryId, token);
    CategoryRequest carCategoryToUpdate = CategoryRequest.builder()
        .parentCategoryId(oilCategoryId)
        .build();
    carCategoryToUpdate.setName("Car");

    //when
    performUpdateRequestAndAssertCycleErrorIsReturned(carCategoryId, carCategoryToUpdate, token);
  }

  @Test
  public void shouldReturnErrorCausedBySettingCategoryToBeSelfParentCategory()
      throws Exception {

    //given
    Category categoryOil = categoryOil();
    long oilCategoryId = callRestToAddCategoryAndReturnId(categoryOil, token);
    CategoryRequest categoryOilToUpdate = CategoryRequest.builder().name(categoryOil.getName())
        .parentCategoryId(oilCategoryId).build();

    //when
    performUpdateRequestAndAssertCycleErrorIsReturned(oilCategoryId, categoryOilToUpdate, token);
  }

  private void performUpdateRequestAndAssertCycleErrorIsReturned(long id, CategoryRequest categoryToUpdate, String token) throws Exception {
    mockMvc
        .perform(put(CATEGORIES_SERVICE_PATH + "/" + id)
            .content(json(categoryToUpdate)).contentType(JSON_CONTENT_TYPE)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", is(getMessage(CATEGORIES_CYCLE_DETECTED))));

  }

  @Test
  public void shouldDeleteCategory() throws Exception {

    //given
    Category categoryHome = categoryHome();
    long homeCategoryId = callRestToAddCategoryAndReturnId(categoryHome, token);
    callRestToAddCategoryAndReturnId(categoryOil(), token);

    //when
    callRestToDeleteCategoryById(homeCategoryId, token);

    //then
    List<Category> categories = callRestToGetAllCategories(token);
    Category deletedCategory = convertCategoryRequestToCategoryAndSetId(homeCategoryId, userId, categoryToCategoryRequest(categoryHome));
    assertThat(categories.size(), is(equalTo(1)));
    assertFalse(categories.contains(deletedCategory));
  }

  @Test
  public void shouldDeleteSubCategoryAndThenParentCategory() throws Exception {

    //given
    long carCategoryId = callRestToAddCategoryAndReturnId(categoryCar(), token);
    long oilCategoryId = callRestToAddCategoryWithSpecifiedParentCategoryIdAndReturnId(categoryOil(), carCategoryId, token);

    //when
    callRestToDeleteCategoryById(oilCategoryId, token);
    callRestToDeleteCategoryById(carCategoryId, token);

    //then
    List<Category> categories = callRestToGetAllCategories(token);
    assertThat(categories.size(), is(0));
  }

  @Test
  public void shouldReturnErrorCausedByTryingToDeleteParentCategoryOfSubCategory() throws Exception {

    //given
    long carCategoryId = callRestToAddCategoryAndReturnId(categoryCar(), token);
    callRestToAddCategoryWithSpecifiedParentCategoryIdAndReturnId(categoryOil(), carCategoryId, token);

    //when
    mockMvc.perform(delete(CATEGORIES_SERVICE_PATH + "/" + carCategoryId)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(getMessage(CANNOT_DELETE_PARENT_CATEGORY)));
  }

  @Test
  public void shouldReturnErrorCausedWrongIdProvidedDeleteMethod() throws Exception {

    //when
    mockMvc.perform(delete(CATEGORIES_SERVICE_PATH + "/" + NOT_EXISTING_ID)
        .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isNotFound());
  }
}
