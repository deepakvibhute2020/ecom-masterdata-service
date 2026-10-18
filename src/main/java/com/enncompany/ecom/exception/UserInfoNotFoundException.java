package com.enncompany.ecom.exception;

public class UserInfoNotFoundException extends RuntimeException {

  public UserInfoNotFoundException(Long id) {
    super("UserInfo not found: " + id);
  }
}

