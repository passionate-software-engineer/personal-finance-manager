import {browser, by, element} from 'protractor';

export class RegisterPage {

  navigateTo() {
    return browser.get('/register');
  }

  registerButton() {
    return element(by.id('RegisterButton'));
  }

  typeFirstName(firstName: string) {
    return element(by.id('RegisterFirstNameInput')).sendKeys(firstName);
  }

  typeLastName(lastName: string) {
    return element(by.id('RegisterLastNameInput')).sendKeys(lastName);
  }

  typeUsername(username: string) {
    return element(by.id('RegisterUsernameInput')).sendKeys(username);
  }


  typePassword(password: string) {
    return element(by.id('RegisterPasswordInput')).sendKeys(password);
  }

  async registerUser(firstName: string, lastName: string, username: string, password: string) {
    await this.navigateTo();
    await this.typeFirstName(firstName);
    await this.typeLastName(lastName);
    await this.typeUsername(username);
    await this.typePassword(password);

    await this.registerButton().click();
  }

}
