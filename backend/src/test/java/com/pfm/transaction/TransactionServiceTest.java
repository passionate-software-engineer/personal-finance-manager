package com.pfm.transaction;

import static com.pfm.helpers.TestTransactionProvider.MOCK_TRANSACTION_ACCOUNT;
import static com.pfm.helpers.TestTransactionProvider.MOCK_TRANSACTION_CATEGORY;
import static com.pfm.helpers.TestTransactionProvider.MOCK_TRANSACTION_DESCRIPTION;
import static com.pfm.helpers.TestTransactionProvider.MOCK_TRANSACTION_ID;
import static com.pfm.helpers.TestTransactionProvider.MOCK_TRANSACTION_PRICE;
import static com.pfm.helpers.TestTransactionProvider.mockTransaction;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {


  @Mock
  private TransactionRepository transactionRepository;

  @InjectMocks
  private TransactionService transactionService;

  @Test
  public void shouldGetTransactionById() {
    //given
    when(transactionRepository.findById(MOCK_TRANSACTION_ID))
        .thenReturn(Optional.of(mockTransaction));

    //when
    Transaction result = transactionService.getTransactionById(MOCK_TRANSACTION_ID).orElse(null);

    //then
    verify(transactionRepository).findById(MOCK_TRANSACTION_ID);
    assertThat(result.getId(), is(equalTo(MOCK_TRANSACTION_ID)));
    assertThat(result.getDescription(), is(equalTo(MOCK_TRANSACTION_DESCRIPTION)));
    assertThat(result.getAccount(), is(equalTo(MOCK_TRANSACTION_ACCOUNT)));
    assertThat(result.getCategory(), is(equalTo(MOCK_TRANSACTION_CATEGORY)));
    assertThat(result.getPrice(), is(equalTo(MOCK_TRANSACTION_PRICE)));
  }

  @Test
  public void shouldGetTransactions() {
    //given
    when(transactionRepository.findAll()).thenReturn(Collections.singletonList(mockTransaction));

    //when
    List<Transaction> result = transactionService.getTransactions();

    //then
    verify(transactionRepository).findAll();
    assertNotNull(result);
    assertThat(result.size(), is(1));
    assertThat(result.get(0), is(equalTo(mockTransaction)));
  }

  @Test
  public void shouldAddTransaction() {
    //given
    when(transactionRepository.save(mockTransaction)).thenReturn(mockTransaction);

    //when
    Transaction result = transactionService.addTransaction(mockTransaction);

    //then
    verify(transactionRepository).save(mockTransaction);
    assertThat(result.getId(), is(equalTo(MOCK_TRANSACTION_ID)));
    assertThat(result.getDescription(), is(equalTo(MOCK_TRANSACTION_DESCRIPTION)));
    assertThat(result.getAccount(), is(equalTo(MOCK_TRANSACTION_ACCOUNT)));
    assertThat(result.getCategory(), is(equalTo(MOCK_TRANSACTION_CATEGORY)));
    assertThat(result.getPrice(), is(equalTo(MOCK_TRANSACTION_PRICE)));
  }

  @Test
  public void shouldUpdateTransaction() {

    //given
    when(transactionRepository.findById(MOCK_TRANSACTION_ID))
        .thenReturn(Optional.of(mockTransaction));
    when(transactionRepository.save(mockTransaction)).thenReturn(mockTransaction);

    //when
    transactionService.updateTransaction(MOCK_TRANSACTION_ID, mockTransaction);

    //then
    verify(transactionRepository).findById(MOCK_TRANSACTION_ID);
    verify(transactionRepository).save(mockTransaction);
  }

  @Test
  public void shouldDeleteTransaction() {
    //given
    doNothing().when(transactionRepository).deleteById(MOCK_TRANSACTION_ID);

    //when
    transactionService.deleteTransaction(MOCK_TRANSACTION_ID);

    //then
    verify(transactionRepository).deleteById(MOCK_TRANSACTION_ID);
  }

  @Test
  public void shouldCheckIfIdExist() {
    //given
    when(transactionRepository.existsById(MOCK_TRANSACTION_ID)).thenReturn(true);

    //when
    transactionService.idExist(MOCK_TRANSACTION_ID);

    //then
    verify(transactionRepository).existsById(MOCK_TRANSACTION_ID);
  }
}