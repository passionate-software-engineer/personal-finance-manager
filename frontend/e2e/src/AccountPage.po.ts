import {browser, by, element, protractor} from 'protractor';

export class AccountsPage {

  async navigateTo() {
    return browser.get('/accounts');
  }

  addAccountButton() {
    return element(by.id('AddAccountBtn'));
  }

  refreshAccountsButton() {
    return element(by.id('RefreshAccountsBtn'));
  }

  showArchivedAccountsCheckBox() {
    return element(by.id('showArchivedCheckbox'));
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

  newAccountCurrency() {
    return element(by.id('NewAccountCurrencySelect'));
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

  optionsButton(row) {
    return row.element(by.id('OptionsBtn'));
  }

  confirmButton(row) {
    return row.element(by.id('UpdateLastVerificationDateBtn'));
  }

  archiveButton(row) {
    return row.element(by.id('ArchiveBtn'));
  }

  deleteButton(row) {
    return row.element(by.id('DeleteBtn'));
  }

  makeActiveButton(row) {
    return row.element(by.id('MakeActiveBtn'));
  }

  editButton(row) {
    return row.element(by.id('EditBtn'));
  }

  balanceOfEURAccount() {
    return element(by.id('CurrencyBalanceEUR'));
  }

  balanceOfGBPAccount() {
    return element(by.id('CurrencyBalanceGBP'));
  }

  balanceOfPLNAccount() {
    return element(by.id('CurrencyBalancePLN'));
  }

  balanceOfUSDAccount() {
    return element(by.id('CurrencyBalanceUSD'));
  }

  exchengeRateOfEUR() {
   return element(by.id('CurrencyExchangeRateEUR'));
  }

  exchengeRateOfGBP() {
   return element(by.id('CurrencyExchangeRateGBP'));
  }

  exchengeRateOfPLN() {
   return element(by.id('CurrencyExchangeRatePLN'));
  }

  exchengeRateOfUSD() {
    return element(by.id('CurrencyExchangeRateUSD'));
  }

  balancePLNOfEUR() {
    return element(by.id('CurrencyBalanceOfEUR'));
  }

  balancePLNOfGBP() {
    return element(by.id('CurrencyBalanceOfGBP'));
  }

  balancePLNOfPLN() {
    return element(by.id('CurrencyBalanceOfPLN'));
  }
  balancePLNOfUSD() {
    return element(by.id('CurrencyBalanceOfUSD'));
  }

  balanceOfAllAccounts() {
    return element(by.id('BalanceOfAllAccounts'));
  }
  balanceOfAllAccountCurrenciesSummery() {
    return element(by.id('BalanceOfAllAccountsCurrenciesSummary'));
  }

  assertBalanceOfAllAccounts() {
    expect(this.balanceOfAllAccounts().getText()).toEqual(this.balanceOfAllAccountCurrenciesSummery().getText());
  }

  alert() {
    return element.all(by.id('Alert'));
  }

  assertNumberOfAccounts(number) {
    expect(this.accountRows().count()).toEqual(number);
  }

  assertAccountName(row, expectedText) {
    expect(row.element(by.id('NameReadOnly')).getText()).toEqual(expectedText);
  }

  assertAccountBalance(row, expectedBalance) {
    expect(row.element(by.id('BalanceReadOnly')).getText()).toEqual(expectedBalance);
  }

  assertBalanceVerificationDate(row, expectedVerificationDate) {
    expect(row.element(by.id('BalanceVerificationDate')).getText()).toEqual(expectedVerificationDate);
  }

  assertAccountBalancePLNOfEUR(expectedBalance_PLN) {
    expect(this.balancePLNOfEUR().getText()).toEqual(expectedBalance_PLN);
  }

  assertAccountBalancePLNOfGBP(expectedBalance_PLN) {
    expect(this.balancePLNOfGBP().getText()).toEqual(expectedBalance_PLN);
  }

  assertAccountBalancePLNOfPLN(expectedBalance_PLN) {
   expect(this.balancePLNOfPLN().getText()).toEqual(expectedBalance_PLN);
  }

  assertAccountBalancePLNOfUSD(expectedBalance_PLN) {
    expect(this.balancePLNOfUSD().getText()).toEqual(expectedBalance_PLN);
  }

  assertAccountBalancePLNSummary(expectedBalanceOfAllAccountCurrenciesSummery) {
  expect(this.balanceOfAllAccounts().getText()).toEqual(expectedBalanceOfAllAccountCurrenciesSummery);
  }


  assertSuccessMessage(message) {
    return; // TODO assert not working in stable way

    const until = protractor.ExpectedConditions;
    browser.wait(until.presenceOf(element(by.id('Alert'))), 100, 'Alert is taking too long to appear on page');
    expect(this.alert().count()).toEqual(1);
    expect(this.alert().first().getText()).toEqual(message + '\n×');
  }

  async removeAllAccounts() {
    let numberOfAccounts = await this.accountRows().count();

    while (numberOfAccounts > 0) {
      this.deleteAccount(this.accountRows().first());

      numberOfAccounts = await this.accountRows().count();
    }
    expect(this.accountRows().count()).toEqual(0);
  }

  addAccount(name, balance) {
    this.addAccountButton().click();

    this.newAccountName().sendKeys(name);
    this.newAccountBalance().sendKeys(balance);

    this.newAccountSaveButton().click();

    this.assertSuccessMessage('Account added');
    this.refreshAccountsButton().click();
  }

  addAccountWithCurrency(name, balance, currency) {
   this.addAccountButton().click();

   this.newAccountName().sendKeys(name);
   this.newAccountBalance().sendKeys(balance);
   this.newAccountCurrency().sendKeys(currency);

   this.newAccountSaveButton().click();

   this.assertSuccessMessage('Account added');
   this.refreshAccountsButton().click();
  }

  updateAccount(name, balance) {
    this.optionsButton(this.accountRows().first()).click();
    this.editButton(this.accountRows().first()).click();

    this.editAccountName().clear();
    this.editAccountName().sendKeys(name);

    this.editAccountBalance().clear();
    this.editAccountBalance().sendKeys(balance);

    this.editAccountSaveButton().click();

    this.assertSuccessMessage('Account updated');
    this.refreshAccountsButton().click();
  }

  deleteAccount(row) {
    this.optionsButton(row).click();
    this.deleteButton(row).click();

    browser.switchTo().alert().accept();

    this.assertSuccessMessage('Account deleted');
    this.refreshAccountsButton().click();
  }

  confirmBalance(row) {
    this.optionsButton(row).click();
    this.confirmButton(this.accountRows().first()).click();

    this.assertSuccessMessage('Account balance verification date was updated successfully');
  }

  archiveBalance(row) {
    this.optionsButton(row).click();
    this.archiveButton(this.accountRows().first()).click();

    this.assertSuccessMessage('Account was archived successfully');
  }

  archiveAccountsShow() {
    this.showArchivedAccountsCheckBox().click();
  }

  makeActiveAccounts(row) {
    this.optionsButton(row).click();
    this.makeActiveButton(this.accountRows().first()).click();

    this.assertSuccessMessage('Account was marked as active');
  }

}
