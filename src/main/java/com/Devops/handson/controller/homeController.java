package com.Devops.handson.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class homeController
{
	@GetMapping
	public String helloAPI()
	{
		return "Hello From DevOps Engineer"+ " " +System.getenv("WELCOME_MESSAGE");
	}
	
	@GetMapping("/home")
	public String message()
	{
		return "Home Page";
	}
	
	@GetMapping("/test")
	public String testing()
	{
		return "Testing page";
	}
	

}
