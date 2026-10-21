package com.enncompany.ecom.dto;

import com.enncompany.ecom.enums.UserType;

/** Login-lookup view of a user; includes the password hash for the auth service only. */
public record UserLoginDto(
    Long id, String loginId, UserType userType, String userPass, String emailId, Boolean active) {}
