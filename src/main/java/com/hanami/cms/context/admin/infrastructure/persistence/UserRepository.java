package com.hanami.cms.context.admin.infrastructure.persistence;

import com.hanami.cms.context.admin.application.jwt.EvergardenEncoder;
import com.hanami.cms.context.admin.domain.entity.Role;
import com.hanami.cms.context.admin.domain.entity.UserMappingInterface;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.davidmoten.rx.jdbc.Database;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.adapter.rxjava.RxJava2Adapter;
import reactor.core.publisher.Mono;

@Component
public class UserRepository {
	
	private Database database;
	
	private Logger logger;
	
	private EvergardenEncoder encoder;
	
	@Autowired
	public UserRepository(Database database, Logger logger, EvergardenEncoder encoder) {
		
		this.database = database;
		this.logger = logger;
		this.encoder = encoder;
	}
	
	public Mono<UserMappingInterface> findByEmail(String email) {
		String sql = "SELECT * FROM user WHERE email = :email";
		
		Single<UserMappingInterface> singleUser = database
			.select(sql)
			.parameter("email", email)
			.autoMap(UserMappingInterface.class)
			.firstOrError();
		
		logger.info("encoded password is " + encoder.encode("password"));
		
		return RxJava2Adapter.singleToMono(singleUser);
	}
	
	public Mono<UserMappingInterface> findById(int id) {
		String sql = "SELECT * FROM user WHERE id = :id";
		
		Single<UserMappingInterface> singleUser = database
			.select(sql)
			.parameter("id", id)
			.autoMap(UserMappingInterface.class)
			.firstOrError();
		
		return RxJava2Adapter.singleToMono(singleUser);
	}
	
	public Mono<UserMappingInterface> create(UserMappingInterface user) {
		String sql = "INSERT IGNORE INTO user (email, password, firstname, lastname, role, activated, salt) " +
			"VALUES(:email, :password, :firstname, :lastname, :role, :activated, :salt) ";
		
		Flowable<Integer> record = database
			.update(sql)
			.parameter("email", user.getEmail())
			.parameter("password", user.getPassword())
			.parameter("firstname", user.getFirstName())
			.parameter("lastname", user.getLastName())
			.parameter("activated", user.isActivated())
			.parameter("role", user.getRole().toString())
			.parameter("salt", user.getSalt())
			.returnGeneratedKeys()
			.getAs(Integer.class);
		
		Single<UserMappingInterface> singleUser = record.flatMap(
			id -> findById(id.intValue())
		).firstOrError();
		
		return RxJava2Adapter.singleToMono(singleUser);
	}
	
	public Mono<UserMappingInterface> findByRole(Role role) {
		String sql = "SELECT * FROM user WHERE role = :role";
		
		Single<UserMappingInterface> singleUser = database
			.select(sql)
			.parameter("role", role.toString())
			.autoMap(UserMappingInterface.class)
			.firstOrError();
		
		return RxJava2Adapter.singleToMono(singleUser);
	}
}
