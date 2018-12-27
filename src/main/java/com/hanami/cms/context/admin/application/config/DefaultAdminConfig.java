package com.hanami.cms.context.admin.application.config;

import com.hanami.cms.context.admin.domain.entity.Role;
import com.hanami.cms.context.admin.domain.entity.User;
import com.hanami.cms.context.admin.infrastructure.persistence.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultAdminConfig {
	
	private UserRepository userRepository;
	
	private Logger logger;
	
	@Autowired
	public DefaultAdminConfig(UserRepository userRepository, Logger logger) {
		this.userRepository = userRepository;
		this.logger = logger;
	}
	
	@Bean
	public void handleContextRefresh() {
		userRepository.findByRole(Role.MASTER_ADMIN)
			.doOnError(throwable -> {
				logger.info("No master admin found auto generate default admin");
				createMasterAdmin();
			})
			.subscribe();
	}
	
	private void createMasterAdmin() {
		User admin = new User();
		
		admin.setEmail("violet@mail.com");
		admin.setPassword("pass");
		admin.setActivated(true);
		admin.setRole(Role.MASTER_ADMIN);
		admin.setFirstname("Violet");
		admin.setLastname("Evergarden");
		
		userRepository.create(admin)
			.doOnError(throwable -> logger.debug("Default Admin already created"))
			.subscribe(user -> logger.debug("Default admin created"));
	}
}
