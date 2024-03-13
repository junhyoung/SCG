package com.example.service1.controller;

import com.example.service1.config.Service1Config;
import com.example.service1.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
