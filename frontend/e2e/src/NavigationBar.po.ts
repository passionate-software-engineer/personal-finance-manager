import {by, element} from 'protractor';

export class NavigationBar {

  pfmLink() {
    return element(by.id('PfmLink'));
  }

  transactionLink() {
    return element(by.id('TransactionLink'));
  }

  accountsLink() {
    return element(by.id('AccountsLink'));
  }

  categoriesLink() {
    return element(by.id('CategoriesLink'));
  }

  navbarDropdown() {
    return element(by.id('navbarDropdown'));
  }

  navbarDropdownLogoutLink() {
    return element(by.id('LogoutLink'));
  }

  historyLink() {
    return element(by.id('HistoryLink'));
  }

  registerLink() {
    return element(by.id('RegisterLink'));
  }

  logoutLink() {
    return element(by.id('LogoutLink'));
  }

  loginLink() {
    return element(by.id('LoginLink'));
  }
}
