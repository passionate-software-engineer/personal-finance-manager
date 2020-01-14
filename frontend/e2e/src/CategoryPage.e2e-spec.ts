import {LoginPage} from './LoginPage.po';
import {RegisterPage} from './RegisterPage.po';
import {v4 as uuid} from 'uuid';
import {CategoryPage} from './CategoryPage.po';
import {AccountsPage} from './AccountPage.po';
import {TransactionAndFilterPage} from './TransactionPage.po';

describe('Category page tests', () => {
  const categoryPage = new CategoryPage();
  const accountPage = new AccountsPage();
  const transactionPage = new TransactionAndFilterPage();

  beforeAll(async () => {
    const registerPage = new RegisterPage();
    const username = 'Username_' + uuid();
    const password = 'Password_' + uuid();
    await registerPage.registerUser('FirstName', 'LastName', username, password);

    const loginPage = new LoginPage();
    await loginPage.loginAs(username, password);
  });

  beforeEach(async () => {

    await transactionPage.navigateTo();
    await transactionPage.removeAllTransactions();

    await accountPage.navigateTo();
    await accountPage.removeAllAccounts();

    await categoryPage.navigateTo();
    await categoryPage.removeAllCategories();

    categoryPage.navigateTo();
  });

  it('should display correct English descriptions on category page', () => {

    // then
    expect(categoryPage.refreshCategoriesButton().getText()).toEqual('Refresh');
    expect(categoryPage.addCategoryButton().getText()).toEqual('Add Category');
    expect(categoryPage.nameHeader().getText()).toEqual('Name ▲');
    expect(categoryPage.parentCategoryHeader().getText()).toEqual('Parent Category');
  });

  it('should add category', () => {
    // given
    const categoryName = 'Test Category';

    // when
    categoryPage.addCategory(categoryName, 'Main Category');

    // then
    categoryPage.assertMessage('Category added');
    categoryPage.assertNumberOfCategories(1);
    categoryPage.assertCategoryName(categoryPage.categoryRowsAll().first(), categoryName);
    categoryPage.assertParentCategory(categoryPage.categoryRowsAll().first(), 'Main Category');

  });

  it('should add category with parent category', () => {

    // given
    const parentCategoryName = 'Car';
    const categoryName = 'Oil';

    // when
    categoryPage.addCategory(parentCategoryName, 'Main Category');
    categoryPage.addCategory(categoryName, parentCategoryName);

    // then
    categoryPage.assertMessage('Category added');
    categoryPage.assertNumberOfCategories(2);
    categoryPage.assertCategoryName(categoryPage.categoryRowsAll().first(), parentCategoryName);
    categoryPage.assertParentCategory(categoryPage.categoryRowsAll().first(), 'Main Category');
    categoryPage.assertCategoryName(categoryPage.categoryRowsAll().last(), categoryName);
    categoryPage.assertParentCategory(categoryPage.categoryRowsAll().last(), parentCategoryName);
  });

  it('should update category without parent category', () => {

    // given
    const updatedCategoryName = 'Oil';

    // when
    categoryPage.addCategory('Car', 'Main Category');
    categoryPage.updateCategory(categoryPage.categoryRowsAll().first(), updatedCategoryName, 'Main Category');

    // then
    categoryPage.assertMessage('Category edited');
    categoryPage.assertNumberOfCategories(1);
    categoryPage.assertCategoryName(categoryPage.categoryRowsAll().first(), updatedCategoryName);
    categoryPage.assertParentCategory(categoryPage.categoryRowsAll().first(), 'Main Category');
  });

  it('should update category with parentCategory change', () => {

    // given
    const parentCategoryName = 'Car';
    const categoryName = 'Oil';
    categoryPage.addCategory(parentCategoryName, 'Main Category');
    categoryPage.addCategory(categoryName, 'Main Category');

    // when
    categoryPage.updateCategory(categoryPage.categoryRowsAll().last(), categoryName, parentCategoryName);

    // then
    categoryPage.assertMessage('Category edited');
    categoryPage.assertNumberOfCategories(2);
    categoryPage.assertCategoryName(categoryPage.categoryRowsAll().last(), categoryName);
    categoryPage.assertParentCategory(categoryPage.categoryRowsAll().last(), parentCategoryName);
  });

  it('should delete category', () => {

    // given
    categoryPage.addCategory('Car', 'Main Category');

    // when
    categoryPage.deleteCategory(categoryPage.categoryRowsAll().first());

    // then
    categoryPage.assertMessage('Category deleted');
    categoryPage.assertNumberOfCategories(0);
  });

  it('should not delete category, because category is parent category', () => {

    // given
    const categoryName = 'Car';
    const parentCategoryName = 'Oil';
    categoryPage.addCategory(parentCategoryName, 'Main Category');
    categoryPage.addCategory(categoryName, parentCategoryName);

    // when
    categoryPage.deleteCategory(categoryPage.categoryRowsAll().last());

    // then
    categoryPage.assertMessage('Category is parent category. Delete not possible - please first delete all subcategories.');
    categoryPage.assertNumberOfCategories(2);
  });

});
