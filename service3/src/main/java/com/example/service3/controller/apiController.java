package com.example.service3.controller;

import com.example.service3.config.Service3Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service3")
@Slf4j
public class apiController {
	@GetMapping("/api1")
	public String api1() {
		System.out.println("Service3 api1");
		return "From Service3 /api1";
	}
	@GetMapping("/api2")
	public String api2() {
		System.out.println("Service3 api2");
		return "From Service3 /api2";
	}

	@GetMapping("/msg")
	public String messge(@RequestHeader("second-request") String header) {
		log.info(header);
		return "From Service3 /msg";
	}

	@GetMapping("/check")
	public String check() {
		return "From Service3 /check";
	}

	@Autowired
	Service3Config config;
	@GetMapping("/configtest")
	public String configTest() {
		return config.getConfigTest();
	}

}
