package com.hanami.cms.infrastructure.publisher;

import com.hanami.cms.entity.publisher.Post;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.davidmoten.rx.jdbc.Database;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.adapter.rxjava.RxJava2Adapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class PostRepository {
	
	private Database database;
	private Logger   logger;
	
	@Autowired
	public PostRepository(Database database, Logger logger) {
		this.database = database;
		this.logger = logger;
	}
	
	public Flux<Post> fetchAll() {
		
		Flowable<Post> flowable = database.select("SELECT * FROM post").get(rs->{
			return new Post(rs.getString("title"), rs.getString("body"), rs.getString("author"));
		});
		
		return Flux.from(flowable);
	}
	
	public Mono<com.hanami.cms.entity.publisher.mapping.Post> fetchById(int id) {
		
		Single<com.hanami.cms.entity.publisher.mapping.Post> single = database
			.select("SELECT id, title, body, author FROM post WHERE id = :id")
			.parameter("id", id)
			.autoMap(com.hanami.cms.entity.publisher.mapping.Post.class)
			.firstOrError();
		
		return RxJava2Adapter.singleToMono(single);
	}
}
