package com.enncompany.ecom.userinfo.service;

import com.enncompany.ecom.dto.UserInfoDto;
import com.enncompany.ecom.entity.UserInfo;
import com.enncompany.ecom.exception.UserInfoNotFoundException;
import com.enncompany.ecom.userinfo.mapper.UserInfoMapper;
import com.enncompany.ecom.userinfo.repository.UserInfoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserInfoServiceImpl implements UserInfoService {

  private final UserInfoRepository repository;
  private final UserInfoMapper mapper;

  public UserInfoServiceImpl(UserInfoRepository repository, UserInfoMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public UserInfoDto create(UserInfoDto dto) {
    return mapper.toDto(repository.save(mapper.toEntity(dto)));
  }

  @Override
  @Transactional(readOnly = true)
  public UserInfoDto getById(Long id) {
    return mapper.toDto(find(id));
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
    return mapper.toDto(repository.save(existing));
  }

  private UserInfo find(Long id) {
    return repository.findById(id).orElseThrow(() -> new UserInfoNotFoundException(id));
  }
}

