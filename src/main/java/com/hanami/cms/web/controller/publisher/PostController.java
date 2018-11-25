package com.hanami.cms.web.controller.publisher;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="${API_V1}/post")
public class PostController {

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity create() {
		return new ResponseEntity(HttpStatus.ACCEPTED);
	}
}
