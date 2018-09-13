import {browser, by, element} from 'protractor';

export class LoginPage {

  navigateTo() {
    return browser.get('/login');
  }

  loginButton() {
    return element(by.id('LoginButton'));
  }

  typeUser(username: string) {
    return element(by.id('LoginUsernameInput')).sendKeys(username);
  }


  typePassword(password: string) {
    return element(by.id('LoginPasswordInput')).sendKeys(password);
  }

  async loginAs(username: string, password: string) {
    await this.typeUser(username);
    await this.typePassword(password);

    await this.loginButton().click();
  }

}
