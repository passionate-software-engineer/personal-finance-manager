import {AccountsPage} from './AccountPage.po';
import {browser, by} from 'protractor';

describe('workspace-project App', () => {
  let page: AccountsPage;

  beforeEach(() => {
    page = new AccountsPage();
    page.navigateTo();
  });

  it('should display correct English descriptions on page elements', () => {
    expect(page.addAccountButton().getText()).toEqual('Add Account');
    expect(page.refreshAccountsButton().getText()).toEqual('Refresh');
    expect(page.nameHeader().getText()).toEqual('Name ▼');
    expect(page.balanceHeader().getText()).toEqual('Balance');

    // page.refreshAccountsButton().click();

    page.accountRows().count().then((numberOfAccounts) => {

      for (let i = numberOfAccounts; i >= 1; i--) {
        page.accountRows().first().element(by.id('OptionsBtn')).click();
        page.accountRows().first().element(by.id('DeleteBtn')).click();
        browser.switchTo().alert().accept();
        expect(page.accountRows().count()).toEqual(i - 1);
      }

      expect(page.accountRows().count()).toEqual(0);

      page.addAccountButton().click();

      page.newAccountBalance().sendKeys('141231.53');
      page.newAccountName().sendKeys('First Test Account');
      page.newAccountSaveButton().click();

      expect(page.accountRows().count()).toEqual(1);

      expect(page.accountRows().first().element(by.id('NameReadOnly')).getText()).toEqual('First Test Account');
      expect(page.accountRows().first().element(by.id('BalanceReadOnly')).getText()).toEqual('141,231.53');

      page.accountRows().first().element(by.id('OptionsBtn')).click();
      page.accountRows().first().element(by.id('EditBtn')).click();

      page.editAccountBalance().clear();
      page.editAccountBalance().sendKeys('231.55');
      page.editAccountName().clear();
      page.editAccountName().sendKeys('First Updated Test Account');
      page.editAccountSaveButton().click();

      expect(page.accountRows().count()).toEqual(1);

      expect(page.accountRows().first().element(by.id('NameReadOnly')).getText()).toEqual('First Updated Test Account');
      expect(page.accountRows().first().element(by.id('BalanceReadOnly')).getText()).toEqual('231.55');
    });

  });
});
