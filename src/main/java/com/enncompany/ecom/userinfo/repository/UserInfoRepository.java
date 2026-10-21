package com.enncompany.ecom.userinfo.repository;

import com.enncompany.ecom.entity.UserInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

  List<UserInfo> findByLoginIdAndActiveTrue(String loginId);
}

