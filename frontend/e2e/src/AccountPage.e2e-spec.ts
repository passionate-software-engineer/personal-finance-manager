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

 it('should check balance PLN', () => {
    // when
    const accountName = 'First Balance PLN Check';
    // given
    page.addAccount(accountName, '250.20');
    // then
    page.assertBalanceOfAllAccounts();
  });

 it('should check account balance currency with  box currencies balance currency', () => {
    // given
    page.addAccountWithCurrency('Balance currency EUR', '300.25', 'EUR');
    page.addAccountWithCurrency('Balance currency GBP', '123.50', 'GBP');
    page.addAccountWithCurrency('Balance currency PLN', '1,250.25', 'PLN');
    page.addAccountWithCurrency('Balance currency USD', '525.75', 'USD');

    // then
    const balanceEUR = page.balanceOfEURAccount().getText();
    const balanceGBP = page.balanceOfGBPAccount().getText();
    const balancePLN = page.balanceOfPLNAccount().getText();
    const balanceUSD = page.balanceOfUSDAccount().getText();
    page.assertAccountBalance(page.accountRows().get(0), balanceEUR);
    page.assertAccountBalance(page.accountRows().get(1), balanceGBP);
    page.assertAccountBalance(page.accountRows().get(2), balancePLN);
    page.assertAccountBalance(page.accountRows().get(3), balanceUSD);
  });

  it('should check box currencies balance PLN', () => {
    // given
    page.addAccountWithCurrency('Balance currency EUR', '300.25', 'EUR');
    page.addAccountWithCurrency('Balance currency GBP', '123.50', 'GBP');
    page.addAccountWithCurrency('Balance currency PLN', '1,250.25', 'PLN');
    page.addAccountWithCurrency('Balance currency USD', '525.75', 'USD');

    // then
    page.assertAccountBalancePLNOfEUR('1,273.06');
    page.assertAccountBalancePLNOfGBP('616.27');
    page.assertAccountBalancePLNOfPLN('1,250.25');
    page.assertAccountBalancePLNOfUSD('1,882.19');
    page.assertAccountBalancePLNSummary('5,021.76');
  });

 it('should check balance verification date', () => {
   // when
    const accountName = 'First confirm balance';
    page.addAccountWithCurrency(accountName, '125.75', 'USD');

    // given
    page.confirmBalance(page.accountRows().first());
     // then
    page.assertAccountBalancePLNOfUSD('450.19');
    const todayDate = new Date().toISOString().split('T')[0];
    page.assertBalanceVerificationDate(page.accountRows().first(), todayDate);
  });


 it('should make active accounts', () => {
    // when
    const accountName = 'First archive account make active';
    page.addAccountWithCurrency(accountName, '125.75', 'USD');
    page.assertNumberOfAccounts(1);
    page.assertAccountName(page.accountRows().first(), accountName);
    page.assertAccountBalance(page.accountRows().first(), '125.75');
    page.assertAccountBalancePLNOfUSD('450.19');
    page.archiveBalance(page.accountRows().first());
    page.assertNumberOfAccounts(0);
    page.archiveAccountsShow();
    page.assertNumberOfAccounts(1);

    // given
    page.makeActiveAccounts(page.accountRows().first());

    // then
    page.assertNumberOfAccounts(1);
    page.assertAccountName(page.accountRows().first(), accountName);
    page.assertAccountBalance(page.accountRows().first(), '125.75');
    page.assertAccountBalancePLNOfUSD('450.19');
  });

  it('should archive accounts and show archived accounts', () => {
    // when
    const accountName = 'First archive account';
    page.addAccountWithCurrency(accountName, '125.75', 'USD');
    page.assertNumberOfAccounts(1);
    page.assertAccountName(page.accountRows().first(), accountName);
    page.assertAccountBalance(page.accountRows().first(), '125.75');
    page.assertAccountBalancePLNOfUSD('450.19');
    page.archiveBalance(page.accountRows().first());
    page.assertNumberOfAccounts(0);

    // given
    page.archiveAccountsShow();

    // then
    page.assertNumberOfAccounts(1);
    page.assertAccountName(page.accountRows().first(), accountName);
    page.assertAccountBalance(page.accountRows().first(), '125.75');
    page.assertAccountBalancePLNOfUSD('450.19');
    page.makeActiveAccounts(page.accountRows().first());
  });

});
