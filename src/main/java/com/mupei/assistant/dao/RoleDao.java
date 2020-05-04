package com.mupei.assistant.dao;

import com.mupei.assistant.model.Role;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RoleDao extends CrudRepository<Role, Long> {

	Optional<Role> findByEmail(String email);

	void deleteByEmail(String email);

	@Modifying
	@Transactional
	@Query("update Role r set "
			+ "r.password = CASE WHEN :#{#role.password} IS NULL THEN r.password ELSE :#{#role.password} END ,"
			+ "r.name = CASE WHEN :#{#role.name} IS NULL THEN r.name ELSE :#{#role.name} END ,"
			+ "r.email = CASE WHEN :#{#role.email} IS NULL THEN r.email ELSE :#{#role.email} END ,"
			+ "r.phone = CASE WHEN :#{#role.phone} IS NULL THEN r.phone ELSE :#{#role.phone} END ,"
			+ "r.image = CASE WHEN :#{#role.image} IS NULL THEN r.image ELSE :#{#role.image} END ,"
			+ "r.qq = CASE WHEN :#{#role.qq} IS NULL THEN r.qq ELSE :#{#role.qq} END ,"
			+ "r.sort = CASE WHEN :#{#role.sort} IS NULL THEN r.sort ELSE :#{#role.sort} END ,"
			+ "r.regTime = CASE WHEN :#{#role.regTime} IS NULL THEN r.regTime ELSE :#{#role.regTime} END ,"
			+ "r.loginTime = CASE WHEN :#{#role.loginTime} IS NULL THEN r.loginTime ELSE :#{#role.loginTime} END ,"
			+ "r.loginIP = CASE WHEN :#{#role.loginIP} IS NULL THEN r.loginIP ELSE :#{#role.loginIP} END ,"
			+ "r.activated = CASE WHEN :#{#role.activated} IS NULL THEN r.activated ELSE :#{#role.activated} END "
			+ "where r.id = :#{#role.id}")
	Integer update(Role role);

	Role findByEmailAndPassword(String roleNumber, String encrypted);

	Role findByPhoneAndPassword(String roleNumber, String encrypted);
}
