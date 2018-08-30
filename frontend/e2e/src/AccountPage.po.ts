import {browser, by, element} from 'protractor';

export class AccountsPage {

  navigateTo() {
    return browser.get('/accounts');
  }

  addAccountButton() {
    return element(by.id('AddAccountBtn'));
  }

  refreshAccountsButton() {
    return element(by.id('RefreshAccountsBtn'));
  }

  nameHeader() {
    return element(by.id('NameHeader'));
  }

  balanceHeader() {
    return element(by.id('BalanceHeader'));
  }

  newAccountName() {
    return element(by.id('NewAccountNameInput'));
  }

  newAccountBalance() {
    return element(by.id('NewAccountBalanceInput'));
  }

  newAccountSaveButton() {
    return element(by.id('NewAccountSaveBtn'));
  }

  editAccountName() {
    return element(by.id('EditAccountNameInput'));
  }

  editAccountBalance() {
    return element(by.id('EditAccountBalanceInput'));
  }

  editAccountSaveButton() {
    return element(by.id('EditAccountSaveBtn'));
  }

  accountRows() {
    return element.all(by.id('AccountRow'));
  }


// .element(by.id('NameReadOnly')

}
