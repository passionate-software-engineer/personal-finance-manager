import {browser, by, element, protractor} from 'protractor';
import {NavigationBar} from './NavigationBar.po';

export class AccountsPage {

  navigationBar = new NavigationBar();

  async navigateTo() {
    return this.navigationBar.accountsLink().click();
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

  accountTypeHeader() {
    return element(by.id('AccountTypeHeader'));
  }

  balanceHeader() {
    return element(by.id('BalanceHeader'));
  }

  newAccountName() {
    return element(by.id('NewAccountNameInput'));
  }

  newAccountType() {
    return element(by.id('NewAccountTypeSelect'));
  }

  newAccountCurrency() {
    return element(by.id('NewAccountCurrencySelect'));
  }

  newAccountBalance() {
    return element(by.id('NewAccountBalanceInput'));
  }

  newAccountBankAccountNumber() {
    return element(by.id('NewAccountBankAccountNumberInput'));
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

  exchangeRateOfEUR() {
    return element(by.id('CurrencyExchangeRateEUR'));
  }

  exchangeRateOfGBP() {
    return element(by.id('CurrencyExchangeRateGBP'));
  }

  exchangeRateOfPLN() {
    return element(by.id('CurrencyExchangeRatePLN'));
  }

  exchangeRateOfUSD() {
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

  accountTypeBalancePLNOfCredit() {
    return element(by.id('AccountTypeBalanceOfCredit'));
  }

  accountTypeBalancePLNOfInvestment() {
    return element(by.id('AccountTypeBalanceOfInvestment'));
  }

  accountTypeBalancePLNOfPersonal() {
    return element(by.id('AccountTypeBalanceOfPersonal'));
  }

  accountTypeBalancePLNOfSaving() {
    return element(by.id('AccountTypeBalanceOfSaving'));
  }

  balanceOfAllAccountsTypeSummery() {
    return element(by.id('BalanceOfAllAccountsTypeSummary'));
  }

  assertBalanceOfAllAccounts() {
    expect(this.balanceOfAllAccounts().getText()).toEqual(this.balanceOfAllAccountCurrenciesSummery().getText());
  }

  assertBalanceOfAllAccountsType() {
    expect(this.balanceOfAllAccounts().getText()).toEqual(this.balanceOfAllAccountsTypeSummery().getText());
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

  assertBankAccountNumber(row, expectedBankAccountNumber) {
    expect(row.element(by.id('BankAccountNumber')).getText()).toEqual(expectedBankAccountNumber);
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

  assertAccountBalancePLNOfCreditAccount(expectedBalanceCreditAccount_PLN) {
    expect(this.accountTypeBalancePLNOfCredit().getText()).toEqual(expectedBalanceCreditAccount_PLN);
  }

  assertAccountBalancePLNOfInvestmentAccount(expectedBalanceInvestmentAccount_PLN) {
    expect(this.accountTypeBalancePLNOfInvestment().getText()).toEqual(expectedBalanceInvestmentAccount_PLN);
  }

  assertAccountBalancePLNOfPersonalAccount(expectedBalancePersonalAccount_PLN) {
    expect(this.accountTypeBalancePLNOfPersonal().getText()).toEqual(expectedBalancePersonalAccount_PLN);
  }

  assertAccountBalancePLNOfSavingAccount(expectedBalanceSavingAccount_PLN) {
    expect(this.accountTypeBalancePLNOfSaving().getText()).toEqual(expectedBalanceSavingAccount_PLN);
  }

  assertAccountBalancePLNSummaryWithAccountType(expectedBalanceOfAllAccountsTypeSummery) {
    expect(this.balanceOfAllAccounts().getText()).toEqual(expectedBalanceOfAllAccountsTypeSummery);
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

  addAccount(name, balance, bankAccountNumber) {
    this.addAccountButton().click();

    this.newAccountName().sendKeys(name);
    this.newAccountBalance().clear();
    this.newAccountBalance().sendKeys(balance);
    this.newAccountBankAccountNumber().sendKeys(bankAccountNumber);
    this.newAccountSaveButton().click();

    this.assertSuccessMessage('Account added');
    this.refreshAccountsButton().click();
  }

  addAccountWithCurrency(name, balance, currency, bankAccountNumber) {
    this.addAccountButton().click();

    this.newAccountName().sendKeys(name);

    this.newAccountBalance().clear();
    this.newAccountBalance().sendKeys(balance);

    this.newAccountCurrency().sendKeys(currency);

    this.newAccountBankAccountNumber().sendKeys(bankAccountNumber);
    this.newAccountSaveButton().click();

    this.assertSuccessMessage('Account added');
    this.refreshAccountsButton().click();
  }

  addAccountWithAccountTypeAndCurrency(name, accountType, balance, currency) {
    this.addAccountButton().click();

    this.newAccountName().sendKeys(name);
    this.newAccountType().sendKeys(accountType);
    this.newAccountBalance().clear();
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
