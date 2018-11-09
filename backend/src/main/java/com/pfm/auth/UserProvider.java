package com.pfm.auth;

import org.springframework.stereotype.Service;

@Service
public class UserProvider {

  private static final ThreadLocal<Long> userThreadLocal = new ThreadLocal<>();

  public void setUser(long userId) {
    userThreadLocal.set(userId);
  }

  public void removeUser() {
    userThreadLocal.set(null);
  }

  public long getCurrentUserId() {
    return userThreadLocal.get();
  }

}
