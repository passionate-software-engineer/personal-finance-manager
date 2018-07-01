package com.pfm.sevices;

import com.pfm.model.Account;
import com.pfm.repositories.AccountRepository;
import com.pfm.services.AccountService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

    private static final String NAME = "Jeff Masters";
    private static final Long ID_1 = 1L;
    private static final BigDecimal BALANCE = BigDecimal.TEN;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Before
    public void beforeTest(){
        initTestGetAccountData();
    }

    @Test
    public void shouldGetAccount() {
        //given

        //when
        Account actualAccount = accountService.getAccountById(ID_1);
        //then
        Assert.assertNotNull(actualAccount);
        Assert.assertEquals(ID_1, actualAccount.getId());
    }

    @Test
    public void shouldGetAllAccounts() {
        //given

        //when
        List<Account> actualAccountsList = accountService.getAccounts();
        //then
        Assert.assertFalse(actualAccountsList.isEmpty());
        Assert.assertEquals(ID_1, actualAccountsList.get(0).getId());
        Assert.assertEquals(NAME, actualAccountsList.get(0).getName());
        Assert.assertEquals(BALANCE, actualAccountsList.get(0).getBalance());
    }

    @Test
    public void shouldSaveAccount() {
        //given

        //when
        Account actualAccount = accountService.addAccount(createMockAccounts().orElse(null));
        //then
        Assert.assertNotNull(actualAccount);
        Assert.assertEquals(ID_1, actualAccount.getId());
    }

    @Test
    public void shouldDeleteAccount() {
        //given

        //when
        accountService.deleteAccount(any());
        //then
        verify(accountRepository, atLeastOnce()).deleteById(any());
    }

    @Test
    public void shouldUpdateAccount() {
        //given

        //when
        accountService.updateAccount(ID_1, createMockAccounts().orElse(null));
        //then
        verify(accountRepository, atLeastOnce()).save(any());
    }

    private void initTestGetAccountData() {
        when(accountRepository.findById(any())).thenReturn(createMockAccounts());
        when(accountRepository.findAll()).thenReturn(Collections.singletonList(createMockAccounts().orElse(null)));
        when(accountRepository.save(any())).thenReturn(createMockAccounts().orElse(null));
    }

    private Optional<Account> createMockAccounts() {
        Account account = new Account();
        account.setId(ID_1);
        account.setName(NAME);
        account.setBalance(BALANCE);
        return Optional.of(account);
    }
}
