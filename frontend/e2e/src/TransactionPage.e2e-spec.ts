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
    accountPage.addAccount('Mbank', 1000, '11195000012006857419590584');

    const ACCOUNT_INDEX = 0;
    const CATEGORY_INDEX = 0;

    // when
    transactionPage.addTransaction('01/01/2018', 'desc', 100, null, ACCOUNT_INDEX, null, CATEGORY_INDEX);

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
    accountPage.addAccount('Mbank', 1000, '11195000012006857419590099');
    accountPage.addAccount('Alior', 500, '11195000012006857419590875');

    const ACCOUNT_MBANK_INDEX = 1;
    const ACCOUNT_ALIOR_INDEX = 0;
    const CATEGORY_INDEX = 0;

    // when
    transactionPage.addTransaction('02/02/2018', 'desc', 100, 50, ACCOUNT_MBANK_INDEX, ACCOUNT_ALIOR_INDEX, CATEGORY_INDEX);

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
    accountPage.addAccount('Mbank', 1000, '11195000012006857419590875');
    accountPage.addAccount('Alior', 500, '11195000012006857419590099');

    accountPage.addAccount('Millenium', 10000, '11195000012006857419590584');
    accountPage.addAccount('Ing', 5000, '11195000012006857419590196');

    const ACCOUNT_ALIOR_INDEX = 0;
    const ACCOUNT_ING_INDEX = 1;
    const ACCOUNT_MBANK_INDEX = 2;
    const ACCOUNT_MILLENIUM_INDEX = 3;
    const CATEGORY_CAR_INDEX = 0;
    const CATEGORY_FOOD_INDEX = 1;

    transactionPage.addTransaction('03/03/2018', 'desc', 100, 50, ACCOUNT_MBANK_INDEX, ACCOUNT_ALIOR_INDEX, CATEGORY_CAR_INDEX);

    // when
    transactionPage.updateTransaction(transactionPage.transactionRows().first(),
      '05/05/2018', 'updated description', 1000, 500, ACCOUNT_MILLENIUM_INDEX, ACCOUNT_ING_INDEX, CATEGORY_FOOD_INDEX);

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
    accountPage.addAccount('Mbank', 1000, '11195000012006857419590196');

    const ACCOUNT_INDEX = 0;
    const CATEGORY_INDEX = 0;

    transactionPage.addTransaction('07/07/2018', 'desc', 100, null, ACCOUNT_INDEX, null, CATEGORY_INDEX);
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
