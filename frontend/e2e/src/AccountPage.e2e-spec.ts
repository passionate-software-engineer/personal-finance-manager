import {AccountsPage} from './AccountPage.po';
import {LoginPage} from './LoginPage.po';
import {RegisterPage} from './RegisterPage.po';
import {v4 as uuid} from 'uuid';

describe('Accounts page tests', () => {
  let page: AccountsPage;

  beforeAll(async () => {
    const registerPage = new RegisterPage();
    const username = 'Username_' + uuid();
    const password = 'Password_' + uuid();
    await registerPage.registerUser('FirstName', 'LastName', username, password);

    const loginPage = new LoginPage();
    await loginPage.loginAs(username, password);
  });

  beforeEach(async () => {
    page = new AccountsPage();
    await page.navigateTo();
    await page.removeAllAccounts();
  });

  it('should display correct English descriptions', () => {

    // then
    expect(page.refreshAccountsButton().getText()).toEqual('Refresh');
    expect(page.addAccountButton().getText()).toEqual('Add Account');
    expect(page.nameHeader().getText()).toEqual('Name ▲');
    expect(page.balanceHeader().getText()).toEqual('Balance');
  });

  it('should add account', () => {
    // given
    const accountName = 'First Test Account';

    // when
    page.addAccount(accountName, '141231.53');

    // then
    page.assertNumberOfAccounts(1);
    page.assertAccountName(page.accountRows().first(), accountName);
    page.assertAccountBalance(page.accountRows().first(), '141,231.53');
  });

  it('should update account', () => {
    // when
    const accountName = 'First Updated Test Account';
    page.addAccount('First Test Account', '141231.53');

    // given
    page.updateAccount(accountName, '231.5');

    // then
    page.assertNumberOfAccounts(1);
    page.assertAccountName(page.accountRows().first(), accountName);
    page.assertAccountBalance(page.accountRows().first(), '231.50');
  });

  it('should delete account', () => {
    // when
    page.addAccount('Account to delete', '0');

    // given
    page.deleteAccount(page.accountRows().first());

    // then
    page.assertNumberOfAccounts(0);
  });
});
