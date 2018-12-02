package com.hanami.cms.context.publisher.infrastructure;

import com.hanami.cms.context.publisher.entity.PostMappingInterface;
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

    public Flux<PostMappingInterface> fetchAll() {

        Flowable<PostMappingInterface> flowable = database
                .select("SELECT * FROM post")
                .autoMap(PostMappingInterface.class);

        return Flux.from(flowable);
    }

    public Mono<PostMappingInterface> fetchById(int id) {

        Single<PostMappingInterface> single = database
                .select("SELECT id, title, body, author FROM post WHERE id = :id")
                .parameter("id", id)
                .autoMap(PostMappingInterface.class)
                .firstOrError();

        return RxJava2Adapter.singleToMono(single);
    }

    public Mono<PostMappingInterface> create(PostMappingInterface post) {

        String createSql = "INSERT INTO POST (AUTHOR, BODY, TITLE) VALUES ( :author , :body , :title )";

        Flowable<Integer> isCreated = database.update(createSql)
                .parameter("author", post.getAuthor())
                .parameter("body", post.getBody())
                .parameter("title", post.getTitle())
                .returnGeneratedKeys()
                .getAs(Integer.class);

        Single<PostMappingInterface> newPost = isCreated.flatMap(id -> {
            return this.fetchById(id.intValue());
        }).firstOrError();

        return RxJava2Adapter.singleToMono(newPost);
    }

    public Mono<PostMappingInterface> update(PostMappingInterface post) {
        String updateSql = "UPDATE POST SET title = :title, author = :author, body = :body WHERE id = :id";

        logger.info("updated " + post.getAuthor() + post.getBody() + post.getId() + post.getTitle());

        Flowable<Integer> isUpdated = database
                .update(updateSql)
                .parameter("title", post.getTitle())
                .parameter("author", post.getBody())
                .parameter("body", post.getBody())
                .parameter("id", post.getId())
                .counts();

        Single<PostMappingInterface> updatedPost = isUpdated
                .flatMap(updatedPostId -> this.fetchById(post.getId()))
                .firstOrError();

        return RxJava2Adapter.singleToMono(updatedPost);
    }

    public Mono<Void> delete(int id) {

        String deleteSql = "DELETE FROM post WHERE id = :id";

        Flowable<Integer> deletedPost = database
                .update(deleteSql)
                .parameter("id", id)
                .counts();

        return Mono.from(deletedPost).then();
    }
}
