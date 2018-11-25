package com.hanami.cms.web.controller.publisher;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="${v1s}/post")
public class PostController {

	private static Logger logger;

	public PostController(Logger logger) {
		this.logger = logger;
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity create() {

		logger.info("post controller created");

		return new ResponseEntity(HttpStatus.ACCEPTED);
	}
}
