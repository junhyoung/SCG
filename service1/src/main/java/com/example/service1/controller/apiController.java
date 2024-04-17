package com.example.service1.controller;

import com.example.service1.config.Service1Config;
import com.example.service1.jwt.JwtTokenProvider;
import com.example.service1.transform.ItfInfo;
import com.example.service1.transform.Transform;
import com.example.service1.util.StringUtils;
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

		ItfInfo test = ItfInfo.builder()
				.fieldName("test")
				.itfType('G')
				.nodeValue(ItfInfo.NodeValue.builder()
						.index(0)
						.size(2)
						.type(StringUtils.TYPE_OBJECT)
						.visible(true)
						.build())
				.nodes(Arrays.asList(
						ItfInfo.builder().fieldName("name").itfType('F').nodeValue(ItfInfo.NodeValue.builder()
								.index(0)
								.size(10)
								.type(StringUtils.TYPE_STRING)
								.visible(true)
								.build()).build(),
						ItfInfo.builder().fieldName("age").itfType('F').nodeValue(ItfInfo.NodeValue.builder()
								.index(10)
								.size(10)
								.type(StringUtils.TYPE_INT)
								.visible(false)
								.build()).build(),
						ItfInfo.builder().fieldName("birth").itfType('F').nodeValue(ItfInfo.NodeValue.builder()
								.index(2)
								.size(10)
								.type(StringUtils.TYPE_INT)
								.visible(true)
								.build()).build(),
						ItfInfo.builder().fieldName("contact").itfType('G').nodeValue(ItfInfo.NodeValue.builder()
										.index(3)
										.size(2)
										.type(StringUtils.TYPE_OBJECT)
										.visible(true).build())
								.nodes( Arrays.asList(
												ItfInfo.builder().fieldName("phone").itfType('F').nodeValue(ItfInfo.NodeValue.builder().index(0).size(20).type(StringUtils.TYPE_STRING).visible(false).build()).build(),
												ItfInfo.builder().fieldName("email").itfType('F').nodeValue(ItfInfo.NodeValue.builder().index(1).size(20).type(StringUtils.TYPE_STRING).visible(true).build()).build(),
												ItfInfo.builder().fieldName("varInt").itfType('F').nodeValue(ItfInfo.NodeValue.builder().index(2).size(20).type(StringUtils.TYPE_INT).visible(true).build()).build(),
												ItfInfo.builder().fieldName("varFloat").itfType('F').nodeValue(ItfInfo.NodeValue.builder().index(3).size(20).fsize(4).type(StringUtils.TYPE_FLOAT).visible(true).build()).build()
										)
								).build())
				).build();

		ItfInfo root = ItfInfo.builder()
				.fieldName("Root")
				.itfType('G')
				.nodeValue(ItfInfo.NodeValue.builder()
						.index(0)
						.size(1)
						.type(StringUtils.TYPE_OBJECT)
						.visible(true)
						.build())
				.nodes(Arrays.asList(test))
				.build();

		String fixedLength = transform.transformJsonToFixedLength(req,root);
		log.info("전문 > {}", fixedLength);
		Map<String, Object> resMap = transform.transformFixedLengthToMap(fixedLength,root);

		return transform.mapToJson(resMap);
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
