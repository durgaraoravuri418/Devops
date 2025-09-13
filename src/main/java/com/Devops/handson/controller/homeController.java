package com.Devops.handson.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class homeController
{
	@GetMapping
	public String helloAPI()
	{
		return "Hello From Devops";
	}

}
