import {LoginPage} from './LoginPage.po';
import {RegisterPage} from './RegisterPage.po';
import {v4 as uuid} from 'uuid';
import {CategoryPage} from './CategoryPage.po';

describe('Category page tests', () => {
  let page: CategoryPage;

  beforeAll(async () => {
    const registerPage = new RegisterPage();
    const username = 'Username_' + uuid();
    const password = 'Password_' + uuid();
    await registerPage.registerUser('FirstName', 'LastName', username, password);

    const loginPage = new LoginPage();
    await loginPage.loginAs(username, password);
  });

  beforeEach(async () => {
    page = new CategoryPage();
    await page.navigateTo();

    await page.removeAllCategories();
  });

  it('should display correct English descriptions on category page', () => {

    // then
    expect(page.refreshCategoriesButton().getText()).toEqual('Refresh');
    expect(page.addCategoryButton().getText()).toEqual('Add Category');
    expect(page.nameHeader().getText()).toEqual('Name ▲');
    expect(page.parentCategoryHeader().getText()).toEqual('Parent Category');
  });

  it('should add category', () => {
    // given
    const categoryName = 'Test Category';

    // when
    page.addCategory(categoryName, 'Main Category');

    // then
    page.assertMessage('Category added');
    page.assertNumberOfCategories(1);
    page.assertCategoryName(page.categoryRowsAll().first(), categoryName);
    page.assertParentCategory(page.categoryRowsAll().first(), 'Main Category');

  });

  it('should add category with parent category', () => {

    // given
    const parentCategoryName = 'Car';
    const categoryName = 'Oil';

    // when
    page.addCategory(parentCategoryName, 'Main Category');
    page.addCategory(categoryName, parentCategoryName);

    // then
    page.assertMessage('Category added');
    page.assertNumberOfCategories(2);
    page.assertCategoryName(page.categoryRowsAll().first(), parentCategoryName);
    page.assertParentCategory(page.categoryRowsAll().first(), 'Main Category');
    page.assertCategoryName(page.categoryRowsAll().last(), categoryName);
    page.assertParentCategory(page.categoryRowsAll().last(), parentCategoryName);
  });

  it('should update category without parent category', () => {

    // given
    const updatedCategoryName = 'Oil';

    // when
    page.addCategory('Car', 'Main Category');
    page.updateCategory(page.categoryRowsAll().first(), updatedCategoryName, 'Main Category');

    // then
    page.assertMessage('Category edited');
    page.assertNumberOfCategories(1);
    page.assertCategoryName(page.categoryRowsAll().first(), updatedCategoryName);
    page.assertParentCategory(page.categoryRowsAll().first(), 'Main Category');
  });

  it('should update category with parentCategory change', () => {

    // given
    const parentCategoryName = 'Car';
    const categoryName = 'Oil';
    page.addCategory(parentCategoryName, 'Main Category');
    page.addCategory(categoryName, 'Main Category');

    // when
    page.updateCategory(page.categoryRowsAll().last(), categoryName, parentCategoryName);

    // then
    page.assertMessage('Category edited');
    page.assertNumberOfCategories(2);
    page.assertCategoryName(page.categoryRowsAll().last(), categoryName);
    page.assertParentCategory(page.categoryRowsAll().last(), parentCategoryName);
  });

  it('should delete category', () => {

    // given
    page.addCategory('Car', 'Main Category');

    // when
    page.deleteCategory(page.categoryRowsAll().first());

    // then
    page.assertMessage('Category deleted');
    page.assertNumberOfCategories(0);
  });

  it('should not delete category, because category is parent category', () => {

    // given
    const categoryName = 'Car';
    const parentCategoryName = 'Oil';
    page.addCategory(parentCategoryName, 'Main Category');
    page.addCategory(categoryName, parentCategoryName);

    // when
    page.deleteCategory(page.categoryRowsAll().last());

    // then
    page.assertMessage('Category is parent category. Delete not possible - please first delete all subcategories.');
    page.assertNumberOfCategories(2);
  });

});
