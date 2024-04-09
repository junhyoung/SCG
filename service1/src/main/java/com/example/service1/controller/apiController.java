package com.example.service1.controller;

import com.example.service1.config.Service1Config;
import com.example.service1.jwt.JwtTokenProvider;
import com.example.service1.transform.ItfInfo;
import com.example.service1.transform.Transform;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/service1")
@Slf4j
public class apiController {
	@GetMapping("/api1")
	public String api1() {
		System.out.println("Service1 api1");
		return "From Service1 /api1";
	}
	@GetMapping("/api2")
	public String api2() {
		System.out.println("Service1 api2");
		return "From Service1 /api2";
	}

	@GetMapping("/msg")
	public String messge(@RequestHeader("first-request") String header) {
		log.info(header);
		return "From Service1 /msg";
	}

	@GetMapping("/check")
	public String check() {
		return "From Service1 /check";
	}

	@Autowired
	Service1Config config;
	@GetMapping("/configtest")
	public String configTest() {
		return config.getConfigTest();
	}

	@GetMapping("/commontest")
	public String commonTest() {
		return config.getCommonTest();
	}

	@Autowired
	Transform transform;
	@PostMapping("/transform")
	public String transform(@RequestBody String req) throws JsonProcessingException {
		log.info("TRANSFORM!!");

		ItfInfo test = new ItfInfo(
				"test",
				'G',
				new ItfInfo.NodeValue(0, 2, "Object", "personalInfo", "Personal Information Group", true, false),
				Arrays.asList(
						new ItfInfo("name", 'F', new ItfInfo.NodeValue(1, 30, "String", "name", "Email of the person", true, false), null),
						new ItfInfo("age", 'F', new ItfInfo.NodeValue(0, 10, "int", "age", "Name of the person", true, false), null),
						new ItfInfo("birth", 'F', new ItfInfo.NodeValue(2, 10, "String", "birth", "Name of the person", true, false), null),
						new ItfInfo("contact", 'G', new ItfInfo.NodeValue(3, 2, "Object", "contact", "Institution Information", true, false),
								Arrays.asList(
										new ItfInfo("phone", 'F', new ItfInfo.NodeValue(1, 20, "String", "name", "name Test Field", true, false), null),
										new ItfInfo("email", 'F', new ItfInfo.NodeValue(0, 20, "String", "test2", "Second Test Field", true, false), null)
				))
				)
		);

		ItfInfo root = new ItfInfo(
				"Root",
				'G',
				new ItfInfo.NodeValue(0, 3, "Object", "root", "Root Node", true, false),
				Arrays.asList(test)
		);

		return transform.transformJsonToFixedLength(req,root);
	}


	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@PostMapping("/token")
	public String login(@RequestBody Map<String,String> m) {

		log.info("call token api");

		// 사용자 인증 DB등..
		String id = m.get("id");
		String pw = m.get("pw");

		String userId = "user";
		String userPw = "1234";

		// 정상 user인 경우
		if (userId.equals(id) && userPw.equals(pw)) {
			// jwt 발급
			String jwt = jwtTokenProvider.createToken(m);
			return jwt;

		} else {
			log.info("토큰 발급 실패");
			return "token 발급 실패";
		}

	}
}
