package com.enncompany.ecom.userinfo.service;

import com.enncompany.ecom.dto.UserInfoDto;
import com.enncompany.ecom.dto.UserLoginDto;
import com.enncompany.ecom.entity.UserInfo;
import com.enncompany.ecom.exception.UserInfoNotFoundException;
import com.enncompany.ecom.userinfo.mapper.UserInfoMapper;
import com.enncompany.ecom.userinfo.repository.UserInfoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@Transactional
public class UserInfoServiceImpl implements UserInfoService {

  private final UserInfoRepository repository;
  private final UserInfoMapper mapper;
  private final PasswordEncoder passwordEncoder;

  public UserInfoServiceImpl(
      UserInfoRepository repository, UserInfoMapper mapper, PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.mapper = mapper;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public UserInfoDto create(UserInfoDto dto) {
    UserInfo entity = mapper.toEntity(dto);
    entity.setUserPass(passwordEncoder.encode(entity.getUserPass()));
    return mapper.toDto(repository.save(entity));
  }

  @Override
  @Transactional(readOnly = true)
  public UserInfoDto getById(Long id) {
    return mapper.toDto(find(id));
  }

  @Override
  @Transactional(readOnly = true)
  public UserLoginDto getByLoginId(String loginId) {
    return repository.findByLoginIdAndActiveTrue(loginId).stream()
        .findFirst()
        .map(
            u ->
                new UserLoginDto(
                    u.getId(),
                    u.getLoginId(),
                    u.getUserType(),
                    u.getUserPass(),
                    u.getEmailId(),
                    u.getActive()))
        .orElseThrow(() -> new UserInfoNotFoundException(loginId));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UserInfoDto> getAll(Pageable pageable) {
    return repository.findAll(pageable).map(mapper::toDto);
  }

  @Override
  public UserInfoDto update(Long id, UserInfoDto dto) {
    UserInfo existing = find(id);
    mapper.apply(existing, dto);
    existing.setUserPass(passwordEncoder.encode(existing.getUserPass()));
    return mapper.toDto(repository.save(existing));
  }

  private UserInfo find(Long id) {
    return repository.findById(id).orElseThrow(() -> new UserInfoNotFoundException(id));
  }
}

