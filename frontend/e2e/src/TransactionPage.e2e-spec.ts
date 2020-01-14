import {LoginPage} from './LoginPage.po';
import {RegisterPage} from './RegisterPage.po';
import {v4 as uuid} from 'uuid';
import {CategoryPage} from './CategoryPage.po';
import {AccountsPage} from './AccountPage.po';
import {TransactionAndFilterPage} from './TransactionPage.po';

describe('Transaction page tests', () => {
  const transactionPage = new TransactionAndFilterPage();
  const accountPage = new AccountsPage();
  const categoryPage = new CategoryPage();

  beforeAll(async () => {
    const registerPage = new RegisterPage();
    const username = 'Username_' + uuid();
    const password = 'Password_' + uuid();
    await registerPage.registerUser('FirstName', 'LastName', username, password);

    const loginPage = new LoginPage();
    await loginPage.loginAs(username, password);
  });

  beforeEach(async () => {

    await transactionPage.navigateTo();
    await transactionPage.removeAllTransactions();

    await accountPage.navigateTo();
    await accountPage.removeAllAccounts();

    await categoryPage.navigateTo();
    await categoryPage.removeAllCategories();

    transactionPage.navigateTo();
  });

  it('should display correct English descriptions on transactions page', () => {

    // then
    expect(transactionPage.dateHeader().getText()).toEqual('Date ▼');
    expect(transactionPage.descriptionHeader().getText()).toEqual('Description');
    expect(transactionPage.priceHeader().getText()).toEqual('Price');
    expect(transactionPage.accountHeader().getText()).toEqual('Account');
    expect(transactionPage.categoryHeader().getText()).toEqual('Category');
    expect(transactionPage.addTransactionButton().getText()).toEqual('Add Transaction');
    expect(transactionPage.refreshTransactionsButton().getText()).toEqual('Refresh');
    expect(transactionPage.addFilterButton().getText()).toEqual('Save as new filter');
    expect(transactionPage.updateFilterButton().getText()).toEqual('Update selected filter');
    expect(transactionPage.resetFilterButton().getText()).toEqual('Reset selected filter');
    expect(transactionPage.deleteFilterButton().getText()).toEqual('Delete filter');

  });

  it('should add transaction', () => {

    // given
    categoryPage.navigateTo();
    categoryPage.addCategory('Car', 'Main Category');

    accountPage.navigateTo();
    accountPage.addAccount('Mbank', 1000);

    // when
    transactionPage.addTransaction('01/01/2018', 'desc', 100, null, 'Mbank', null, 'Car');

    // then
    expect(transactionPage.transactionRows().count()).toEqual(1);

    transactionPage.assertDescription('desc');
    transactionPage.assertDate('01/01/2018');
    transactionPage.assertPrices('100.00 EUR (424.00 PLN)', null);
    transactionPage.assertAccounts('Mbank', null);
    transactionPage.assertCategory('Car');

    accountPage.navigateTo();
    accountPage.assertAccountBalance(accountPage.accountRows().get(0), '1,100.00');
  });

  it('should add transaction with two accounts', () => {

    // given
    categoryPage.navigateTo();
    categoryPage.addCategory('Car', 'Main Category');

    accountPage.navigateTo();
    accountPage.addAccount('Mbank', 1000);
    accountPage.addAccount('Alior', 500);

    // when
    transactionPage.addTransaction('02/02/2018', 'desc', 100, 50, 'Mbank', 'Alior', 'Car');

    // then
    expect(transactionPage.transactionRows().count()).toEqual(1);

    transactionPage.assertDescription('desc');
    transactionPage.assertDate('02/02/2018');
    transactionPage.assertPrices('100.00 EUR (424.00 PLN)', '50.00 EUR (212.00 PLN)');
    transactionPage.assertAccounts('Mbank', 'Alior');
    transactionPage.assertCategory('Car');

    accountPage.navigateTo();
    accountPage.assertAccountBalance(accountPage.accountRows().get(0), '550.00');
    accountPage.assertAccountBalance(accountPage.accountRows().get(1), '1,100.00');
  });

  it('should update transaction ', () => {

    // given
    categoryPage.navigateTo();
    categoryPage.addCategory('Car', 'Main Category');
    categoryPage.addCategory('Food', 'Main Category');

    accountPage.navigateTo();
    accountPage.addAccount('Mbank', 1000);
    accountPage.addAccount('Alior', 500);

    accountPage.addAccount('Millenium', 10000);
    accountPage.addAccount('Ing', 5000);

    transactionPage.addTransaction('03/03/2018', 'desc', 100, 50, 'Mbank', 'Alior', 'Car');

    // when
    transactionPage.updateTransaction(transactionPage.transactionRows().first(),
      '05/05/2018', 'updated description', 1000, 500, 'Millenium', 'Ing', 'Food');

    // then
    expect(transactionPage.transactionRows().count()).toEqual(1);

    transactionPage.assertDescription('updated description');
    transactionPage.assertDate('05/05/2018');
    transactionPage.assertPrices('1,000.00 EUR (4,240.00 PLN)', '500.00 EUR (2,120.00 PLN)');
    transactionPage.assertAccounts('Millenium', 'Ing');
    transactionPage.assertCategory('Food');

    accountPage.navigateTo();
    accountPage.assertAccountBalance(accountPage.accountRows().get(0), '500.00');
    accountPage.assertAccountBalance(accountPage.accountRows().get(1), '5,500.00');
    accountPage.assertAccountBalance(accountPage.accountRows().get(2), '1,000.00');
    accountPage.assertAccountBalance(accountPage.accountRows().get(3), '11,000.00');
  });

  it('should delete transaction', () => {

    // given
    categoryPage.navigateTo();
    categoryPage.addCategory('Car', 'Main Category');

    accountPage.navigateTo();
    accountPage.addAccount('Mbank', 1000);

    transactionPage.addTransaction('07/07/2018', 'desc', 100, null, 'Mbank', null, 'Car');
    expect(transactionPage.transactionRows().count()).toEqual(1);

    // when
    transactionPage.navigateTo();
    transactionPage.deleteTransaction(transactionPage.transactionRows().first());

    // then
    expect(transactionPage.transactionRows().count()).toEqual(0);
    accountPage.navigateTo();
    accountPage.assertAccountBalance(accountPage.accountRows().first(), '1,000.00');
  });


});
