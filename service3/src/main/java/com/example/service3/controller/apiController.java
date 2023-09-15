package com.example.service3.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service3")
public class apiController {
	@GetMapping("/api1")
	public String api1() {
		System.out.println("Service3 api1");
		return "From Service3 /service";
	}
	@GetMapping("/api2")
	public String api2() {
		System.out.println("Service3 api2");
		return "From Service3 /service1";
	}
}
