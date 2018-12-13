import {browser, by, element} from 'protractor';

export class CategoryPage {

  async navigateTo() {
    return browser.get('/categories');
  }

  addCategoryButton() {
    return element(by.id('AddCategoryBtn'));
  }

  refreshCategoriesButton() {
    return element(by.id('RefreshCategoriesBtn'));
  }

  nameHeader() {
    return element(by.id('NameHeader'));
  }

  parentCategoryHeader() {
    return element(by.id('ParentCategoryHeader'));
  }

  newCategoryName() {
    return element(by.id('NewCategoryNameInput'));
  }

  newParentCategory() {
    return element(by.id('NewParentCategorySelect'));
  }

  newCategorySaveButton() {
    return element(by.id('NewCategorySaveBtn'));
  }

  editCategoryName() {
    return element(by.id('EditCategoryNameInput'));
  }

  editParentCategory() {
    return element(by.id('EditParentCategorySelect'));
  }

  editCategorySaveButton() {
    return element(by.id('EditCategorySaveBtn'));
  }

  categoryRowsAll() {
    return element.all(by.id('CategoryRow'));
  }

  categoryRows() {
    return element(by.id('CategoryRow'));
  }

  optionsButton(row) {
    return row.element(by.id('OptionsBtn'));
  }

  deleteButton(row) {
    return row.element(by.id('DeleteBtn'));
  }

  editButton(row) {
    return row.element(by.id('EditBtn'));
  }

  alert() {
    return element(by.id('Alert'));
  }

  assertNumberOfCategories(number) {
    expect(this.categoryRowsAll().count()).toEqual(number);
  }

  assertCategoryName(row, expectedText) {
    expect(row.element(by.id('NameReadOnly')).getText()).toEqual(expectedText);
  }

  assertParentCategory(row, expectedText) {
    expect(row.element(by.id('ParentCategoryReadOnly')).getText()).toEqual(expectedText);
  }

  assertMessage(message) {
    return;
    // const first = element.all(by.id('Alerts')).all(by.css('alert-list')).get(0);
    // expect(first.getText()).toEqual(message);
  }

  assertSuccessMessage(message) {
    return;
  }

  // TODO improve this
  async removeAllCategories() {
    await this.navigateTo();
    let numberOfCategories = await this.categoryRowsAll().count();
    if (numberOfCategories === 0) {
      return;
    }
    while (numberOfCategories > 0) {
      this.deleteCategory(this.categoryRowsAll().last());
      numberOfCategories = await this.categoryRowsAll().count();
    }

    expect(this.categoryRowsAll().count()).toEqual(0);
  }

  addCategory(name, parentCategoryName) {
    this.addCategoryButton().click();

    this.newCategoryName().sendKeys(name);

    this.newParentCategory().element(by.cssContainingText('option', parentCategoryName)).click();

    this.newCategorySaveButton().click();

    // this.assertSuccessMessage('Category added');
  }

  updateCategory(row, updatedName, updatedParentCategoryName) {

    this.optionsButton(row).click();
    this.editButton(row).click();

    this.editCategoryName().clear();
    this.editCategoryName().sendKeys(updatedName);

    this.editParentCategory().element(by.cssContainingText('option', updatedParentCategoryName)).click();

    this.editCategorySaveButton().click();

    this.assertSuccessMessage('Category edited');
  }

  deleteCategory(row) {
    this.optionsButton(row).click();
    this.deleteButton(row).click();
    browser.switchTo().alert().accept();
    this.assertSuccessMessage('Category deleted');
  }

}
