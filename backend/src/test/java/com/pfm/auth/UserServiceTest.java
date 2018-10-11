//package com.pfm.auth;
//
//import static org.powermock.api.mockito.PowerMockito.spy;
//import static org.powermock.api.mockito.PowerMockito.when;
//
//import java.security.NoSuchAlgorithmException;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.powermock.api.mockito.PowerMockito;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;
//
//@RunWith(PowerMockRunner.class)
//@PrepareForTest(fullyQualifiedNames = "com.pfm.auth.UserService")
//public class UserServiceTest {
//
//
//  @Test
//  public void bla() throws Exception {
//
//    PowerMockito.mockStatic(UserService.class);
//    when(UserService.getSha512SecurePassword("dddd")).thenThrow(new NoSuchAlgorithmException());
//  }
//}
