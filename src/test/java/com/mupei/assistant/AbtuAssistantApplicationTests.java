package com.mupei.assistant;

import com.mupei.assistant.dao.RoleDao;
import com.mupei.assistant.model.Role;
import com.mupei.assistant.utils.TimeUtil;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
class AbtuAssistantApplicationTests {
	@Autowired private RoleDao roleDao;
	@Autowired private TimeUtil timeUtil;

	@Test
	void insertRoleTest(){
		Role role = new Role();
		role.setActivated(true);
		role.setEmail("2980085672@qq.com");
		role.setName("Test");
		role.setPassword("123456789");
		role.setRegTime(timeUtil.getCurrentTime());
		role.setSort("s");
		roleDao.save(role);
	}

	@Test
	void updateRoleTest(){
		Role testRole = new Role();
		testRole.setId(4L);
		testRole.setName("小明");
		roleDao.update(testRole);
	}

	@Test
	void findRoleTest(){
		roleDao.findByEmailAndPassword("2980085672@qq.com", "123456789");
	}

	@Test
	@Rollback(false)
	@Transactional
	void deleteRoleTest(){
		roleDao.deleteByEmail("2980085672@qq.com");
	}
}
