package com.enncompany.ecom.userinfo.service;

import com.enncompany.ecom.dto.UserInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserInfoService {

  UserInfoDto create(UserInfoDto dto);

  UserInfoDto getById(Long id);

  Page<UserInfoDto> getAll(Pageable pageable);

  UserInfoDto update(Long id, UserInfoDto dto);
}

