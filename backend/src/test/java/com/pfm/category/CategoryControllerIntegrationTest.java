package com.pfm.category;

import static com.pfm.category.CategoryController.convertToCategory;
import static com.pfm.config.MessagesProvider.CANNOT_DELETE_PARENT_CATEGORY;
import static com.pfm.config.MessagesProvider.CATEGORIES_CYCLE_DETECTED;
import static com.pfm.config.MessagesProvider.CATEGORY_WITH_PROVIDED_NAME_ALREADY_EXISTS;
import static com.pfm.config.MessagesProvider.EMPTY_CATEGORY_NAME;
import static com.pfm.config.MessagesProvider.PROVIDED_PARENT_CATEGORY_NOT_EXIST;
import static com.pfm.config.MessagesProvider.getMessage;
import static com.pfm.helpers.TestCategoryProvider.CHILD_CATEGORY_SNICKERS;
import static com.pfm.helpers.TestCategoryProvider.PARENT_CATEGORY_FOOD;
import static com.pfm.helpers.TestCategoryProvider.PARENT_CATEGORY_TO_ADD;
import static com.pfm.helpers.TestCategoryProvider.SUBCATEGORY_TO_ADD;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfm.category.CategoryController.CategoryRequest;
import java.util.List;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.flywaydb.core.Flyway;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(JUnitParamsRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerIntegrationTest {

  private static final String CATEGORIES_SERVICE_PATH = "/categories";
  private static final MediaType CONTENT_TYPE = MediaType.APPLICATION_JSON_UTF8;
  private static final Long NOT_EXISTING_ID = 0L;

  @ClassRule
  public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

  @Rule
  public final SpringMethodRule springMethodRule = new SpringMethodRule();

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

  @Test
  public void shouldAddCategory() throws Exception {
    //given

    Category expectedParentCategory = new Category(null, "Car", null);
    Category expectedSubCategory =
        new Category(null, "Oil", new Category(null, "Car", null));

    //when
    long addedParentCategoryId = callRestServiceToAddCategoryAndReturnId(PARENT_CATEGORY_TO_ADD);
    // TODO that can be hidden in callRestServiceToAddCategoryAndReturnId method, just pass id of parent category to it, don't set it before
    SUBCATEGORY_TO_ADD.setParentCategoryId(
        addedParentCategoryId);
    long addedSubCategoryId = callRestServiceToAddCategoryAndReturnId(SUBCATEGORY_TO_ADD);

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
  @Parameters(method = "emptyAccountNameParameters")
  public void shouldReturnErrorCauseByEmptyNameFiled(String name) throws Exception {
    //given
    CategoryRequest categoryToAdd = CategoryRequest.builder().name(name).build();

    //when
    mockMvc
        .perform(
            post(CATEGORIES_SERVICE_PATH)
                .content(json(categoryToAdd))
                .contentType(CONTENT_TYPE)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", is(getMessage(EMPTY_CATEGORY_NAME))));
  }

  private Object[] emptyAccountNameParameters() {
    return new Object[]{"", " ", "    ", null};
  }

  @Test
  public void shouldReturnErrorCausedByNameAlreadyExist() throws Exception {
    //given
    callRestServiceToAddCategoryAndReturnId(PARENT_CATEGORY_FOOD);

    //when
    mockMvc
        .perform(
            post(CATEGORIES_SERVICE_PATH)
                .content(json(PARENT_CATEGORY_FOOD))
                .contentType(CONTENT_TYPE)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", is(getMessage(CATEGORY_WITH_PROVIDED_NAME_ALREADY_EXISTS))));
  }

  @Test
  public void shouldGetCategories() throws Exception {
    //given
    callRestServiceToAddCategoryAndReturnId(PARENT_CATEGORY_FOOD);
    callRestServiceToAddCategoryAndReturnId(CHILD_CATEGORY_SNICKERS);

    mockMvc
        .perform(get(CATEGORIES_SERVICE_PATH))
        .andExpect(content().contentType(CONTENT_TYPE))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", Matchers.is(1)))
        .andExpect(jsonPath("$[0].name", Matchers.is("Food")))
        .andExpect(jsonPath("$[1].id", Matchers.is(2)))
        .andExpect(jsonPath("$[1].name", Matchers.is("Snickers")));
  }

  @Test
  public void shouldGetParentCategoryById() throws Exception {
    //given
    long id = callRestServiceToAddCategoryAndReturnId(PARENT_CATEGORY_FOOD);

    mockMvc
        .perform(get(CATEGORIES_SERVICE_PATH + "/" + id))
        .andExpect(content().contentType(CONTENT_TYPE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)));
  }

  @Test
  public void shouldGetSubCategoryById() throws Exception {
    //given
    long parentId = callRestServiceToAddCategoryAndReturnId(PARENT_CATEGORY_FOOD);
    CHILD_CATEGORY_SNICKERS.setParentCategoryId(parentId);
    long id = callRestServiceToAddCategoryAndReturnId(CHILD_CATEGORY_SNICKERS);

    //when
    mockMvc
        .perform(get(CATEGORIES_SERVICE_PATH + "/" + id))
        .andExpect(content().contentType(CONTENT_TYPE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", Matchers.is(2)));
  }

  @Test
  public void shouldReturnErrorCausedWrongIdProvidedGetMethod() throws Exception {
    //given
    long id = callRestServiceToAddCategoryAndReturnId(PARENT_CATEGORY_FOOD);

    //when
    mockMvc
        .perform(get(CATEGORIES_SERVICE_PATH + "/" + id + 1))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldUpdateCategoryParentCategory() throws Exception {
    //given
    long id = callRestServiceToAddCategoryAndReturnId(PARENT_CATEGORY_FOOD);
    PARENT_CATEGORY_FOOD.setName("Changed Name");

    //when
    mockMvc.perform(put(CATEGORIES_SERVICE_PATH + "/" + id)
        .contentType(CONTENT_TYPE)
        .content(json(PARENT_CATEGORY_FOOD)))
        .andExpect(status().isOk());

    mockMvc
        .perform(get(CATEGORIES_SERVICE_PATH + "/" + id)
            .content(json(PARENT_CATEGORY_FOOD))
            .contentType(CONTENT_TYPE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", Matchers.is(1)))
        .andExpect(jsonPath("$.name", Matchers.is("Changed Name")));
  }

  @Test
  public void shouldUpdateSubCategory() throws Exception {
    //given
    long primaryParentid = callRestServiceToAddCategoryAndReturnId(PARENT_CATEGORY_FOOD);
    CHILD_CATEGORY_SNICKERS.setParentCategoryId(primaryParentid);
    long childCategoryId = callRestServiceToAddCategoryAndReturnId(CHILD_CATEGORY_SNICKERS);
    CategoryRequest secondParentCategory = CategoryRequest.builder()
        .name("Second Parent Category")
        .build();
    long secondParentCategoryId = callRestServiceToAddCategoryAndReturnId(secondParentCategory);

    CategoryRequest categoryToUpdate = CHILD_CATEGORY_SNICKERS;
    categoryToUpdate.setName("Changed Name");
    categoryToUpdate.setParentCategoryId(secondParentCategoryId);

    Category expectedCategory = convertToCategory(categoryToUpdate);
    expectedCategory.setId(childCategoryId);
    expectedCategory.getParentCategory().setName(secondParentCategory.getName());

    //when
    mockMvc
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
    callRestServiceToAddCategoryAndReturnId(CHILD_CATEGORY_SNICKERS);

    //when
    mockMvc
        .perform(put(CATEGORIES_SERVICE_PATH + "/" + NOT_EXISTING_ID)
            .content(json(CHILD_CATEGORY_SNICKERS)).contentType(CONTENT_TYPE))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingParentCategoryIdProvided() throws Exception {
    //given
    long id = callRestServiceToAddCategoryAndReturnId(CHILD_CATEGORY_SNICKERS);
    CHILD_CATEGORY_SNICKERS.setParentCategoryId(NOT_EXISTING_ID);

    //when
    mockMvc
        .perform(put(CATEGORIES_SERVICE_PATH + "/" + id)
            .content(json(CHILD_CATEGORY_SNICKERS)).contentType(CONTENT_TYPE))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", is(getMessage(PROVIDED_PARENT_CATEGORY_NOT_EXIST))));
  }

  @Test
  public void shouldReturnErrorCausedByCycling() throws Exception {
    //given
    long id = callRestServiceToAddCategoryAndReturnId(PARENT_CATEGORY_FOOD);
    CategoryRequest categoryToUpdate = CategoryRequest.builder().name("Drinks")
        .parentCategoryId(id).build();

    //when
    performUpdateRequestAndAssertCycleErrorIsReturned(categoryToUpdate);
  }

  @Test
  public void shouldReturnErrorCausedBySettingCategoryToBeSelfParentCategory()
      throws Exception {
    //given
    long id = callRestServiceToAddCategoryAndReturnId(PARENT_CATEGORY_FOOD);
    CategoryRequest categoryToUpdate = CategoryRequest.builder().name(PARENT_CATEGORY_FOOD.getName())
        .parentCategoryId(id).build();

    //when
    performUpdateRequestAndAssertCycleErrorIsReturned(categoryToUpdate);
  }

  private void performUpdateRequestAndAssertCycleErrorIsReturned(CategoryRequest categoryToUpdate) throws Exception {
    //when
    mockMvc
        .perform(put(CATEGORIES_SERVICE_PATH + "/" + categoryToUpdate.getParentCategoryId())
            .content(json(categoryToUpdate)).contentType(CONTENT_TYPE))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", is(getMessage(CATEGORIES_CYCLE_DETECTED))));
  }

  @Test
  public void shouldDeleteCategory() throws Exception {
    //given
    long parentCategoryId = callRestServiceToAddCategoryAndReturnId(PARENT_CATEGORY_FOOD);
    CHILD_CATEGORY_SNICKERS.setParentCategoryId(parentCategoryId);
    long childCategoryId = callRestServiceToAddCategoryAndReturnId(CHILD_CATEGORY_SNICKERS);

    //when
    this.mockMvc
        .perform(delete(CATEGORIES_SERVICE_PATH + "/" + childCategoryId))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldDeleteSubCategoryAndThenParentCategory() throws Exception {
    //given
    long primaryParentId = callRestServiceToAddCategoryAndReturnId(PARENT_CATEGORY_FOOD);
    CHILD_CATEGORY_SNICKERS.setParentCategoryId(primaryParentId);
    long childCategoryId = callRestServiceToAddCategoryAndReturnId(CHILD_CATEGORY_SNICKERS);
    //when
    deleteCategoryById(childCategoryId);
    deleteCategoryById(primaryParentId);
    //then
    List<Category> categories = getAllCategoriesFromDatabase();

    assertThat(categories.size(), is(equalTo(0)));
    assertFalse(categories.contains(
        CHILD_CATEGORY_SNICKERS)); // TODo http://hamcrest.org/JavaHamcrest/javadoc/1.3/org/hamcrest/core/IsCollectionContaining.html
    assertFalse(categories.contains(PARENT_CATEGORY_FOOD));
  }

  @Test
  public void shouldReturnErrorCausedByTryingToDeleteParentCategoryOfSubCategory() throws Exception {
    //given
    long parentCategoryId = callRestServiceToAddCategoryAndReturnId(PARENT_CATEGORY_FOOD);
    CHILD_CATEGORY_SNICKERS.setParentCategoryId(parentCategoryId);
    callRestServiceToAddCategoryAndReturnId(CHILD_CATEGORY_SNICKERS);
    //when
    mockMvc.perform(delete(CATEGORIES_SERVICE_PATH + "/" + parentCategoryId))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(getMessage(CANNOT_DELETE_PARENT_CATEGORY)));
  }

  @Test
  public void shouldReturnErrorCausedWrongIdProvidedDeleteMethod() throws Exception {
    //when
    mockMvc.perform(delete(CATEGORIES_SERVICE_PATH + "/" + NOT_EXISTING_ID))
        .andExpect(status().isNotFound());
  }

  private long callRestServiceToAddCategoryAndReturnId(CategoryRequest category) throws Exception {
    String response = mockMvc
        .perform(
            post(CATEGORIES_SERVICE_PATH)
                .content(json(category))
                .contentType(CONTENT_TYPE))
        .andExpect(status().isOk()).andReturn()
        .getResponse().getContentAsString();
    return Long.parseLong(response);
  }

  private Category getCategoryById(long id) throws Exception {
    String response = mockMvc.perform(get(CATEGORIES_SERVICE_PATH + "/" + id))
        .andExpect(content().contentType(CONTENT_TYPE))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return jsonToCategory(response);
  }

  private List<Category> getAllCategoriesFromDatabase() throws Exception {
    String response = mockMvc.perform(get(CATEGORIES_SERVICE_PATH))
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
