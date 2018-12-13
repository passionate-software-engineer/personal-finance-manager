import {NavigationPage} from './NavigationPage.po';
import {LoginPage} from './LoginPage.po';

xdescribe('Login page tests', () => { // TODO - enable after fixing Login tests
  let page: LoginPage;
  let navigationPage: NavigationPage;

  beforeAll(async () => {
    navigationPage = new NavigationPage();
    navigationPage.navbarDropdown().click();
    navigationPage.navbarDropdownLogoutLink().click();
    page = new LoginPage();
  });

  it('should display correct English descriptions on login page', () => {

    // then
    expect(navigationPage.pfmLink().getText()).toEqual('Personal Finance Manager');
    expect(navigationPage.transactionLink().getText()).toEqual('Transactions');
    expect(navigationPage.accountsLink().getText()).toEqual('Accounts');
    expect(navigationPage.historyLink().getText()).toEqual('History');
    expect(navigationPage.registerLink().getText()).toEqual('Register');
    expect(navigationPage.loginLink().getText()).toEqual('Login');

    expect(page.loginButton().getText()).toEqual('Login');
    expect(page.registerLink().getText()).toEqual('Register');

    expect(page.usernameLabel().getText()).toEqual('Username');
    expect(page.passwordLabel().getText()).toEqual('Password');
  });

});
