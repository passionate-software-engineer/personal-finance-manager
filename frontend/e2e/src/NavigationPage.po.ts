import {by, element} from 'protractor';

export class NavigationPage {

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

  historyLink() {
    return element(by.id('HistoryLink'));
  }

}
