package com.mupei.assistant.dao;

import com.mupei.assistant.model.Role;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface RoleDao extends CrudRepository<Role, Long> {
  Boolean existsByEmailAndPassword(String paramString1, String paramString2);
  
  Boolean existsByPhoneAndPassword(String paramString1, String paramString2);
  
  Boolean existsByEmail(String paramString);
  
  Optional<Role> findByEmail(String paramString);
  
  void deleteRoleByEmail(String paramString);
}
