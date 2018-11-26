import {browser, by, element} from 'protractor';

export class TransactionAndFilterPage {

  async navigateTo() {
    return browser.get('/transactions');
  }

  addTransactionButton() {
    return element(by.id('AddTransactionBtn'));
  }

  refreshTransactionsButton() {
    return element(by.id('RefreshTransactionsBtn'));
  }

  dateHeader() {
    return element(by.id('DateHeader'));
  }

  nameHeader() {
    return element(by.id('NameHeader'));
  }

  priceHeader() {
    return element(by.id('PriceHeader'));
  }

  accountHeader() {
    return element(by.id('AccountHeader'));
  }

  categoryHeader() {
    return element(by.id('CategoryHeader'));
  }

  newTransactionDateInput() {
    return element(by.id('NewTransactionDateInput'));
  }

  newTransactionDescriptionInput() {
    return element(by.id('NewTransactionDescriptionInput'));
  }

  newTransactionPriceInput() {
    return element.all(by.id('NewTransactionPriceInput'));
  }

  newTransactionAccountSelects() {
    return element.all(by.id('newTransactionAccountSelects'));
  }

  newTransactionCategorySelect() {
    return element(by.id('newTransactionCategorySelect'));
  }

  newTransactionSaveButton() {
    return element(by.id('newTransactionSaveButton'));
  }

  editTransactionDateInput() {
    return element(by.id('EditTransactionDateInput'));
  }

  editTransactionDescriptionInput() {
    return element(by.id('EditTransactionDescriptionInput'));
  }

  editTransactionPriceInput() {
    return element.all(by.id('EditTransactionPriceInput'));
  }

  editTransactionAccountSelects() {
    return element.all(by.id('EditTransactionAccountSelects'));
  }

  editTransactionCategorySelect() {
    return element(by.id('EditTransactionCategorySelect'));
  }

  newTransactionSaveButton() {
    return element(by.id('newTransactionSaveButton'));
  }

  editTransactionSaveButton() {
    return element(by.id('EditTransactionSaveBtn'));
  }

  TransactionRows() {
    return element.all(by.id('TransactionRow'));
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
    return element.all(by.id('Alert'));
  }


}
