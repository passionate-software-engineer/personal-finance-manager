import {LoginPage} from './LoginPage.po';
import {NavigationBar} from './NavigationBar.po';
import {browser} from 'protractor';

describe('Login page tests', () => {
  let loginPage: LoginPage;
  let navigationBar: NavigationBar;

  beforeEach(async () => {
    browser.manage().window().setSize(1600, 1000); // for some reason this test fails without explicit size

    loginPage = new LoginPage();
    await loginPage.navigateTo();

    navigationBar = new NavigationBar();
    navigationBar.navbarDropdown().click();
    navigationBar.navbarDropdownLogoutLink().click();
  });

  it('should display correct English descriptions on login loginPage', () => {
    // then
    expect(loginPage.loginButton().getText()).toEqual('Login');
    expect(loginPage.registerLink().getText()).toEqual('Register');

    expect(loginPage.usernameLabel().getText()).toEqual('Username');
    expect(loginPage.passwordLabel().getText()).toEqual('Password');

    expect(navigationBar.pfmLink().getText()).toEqual('Personal Finance Manager');
    expect(navigationBar.transactionLink().getText()).toEqual('Transactions');
    expect(navigationBar.accountsLink().getText()).toEqual('Accounts');
    expect(navigationBar.categoriesLink().getText()).toEqual('Categories');
    expect(navigationBar.historyLink().getText()).toEqual('History');
    expect(navigationBar.registerLink().getText()).toEqual('Register');
    expect(navigationBar.loginLink().getText()).toEqual('Login');
  });

});
