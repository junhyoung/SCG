package com.example.service1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service1")
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
}
