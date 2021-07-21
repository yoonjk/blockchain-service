package com.ibm.fabric.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.fabric.model.Car;
import com.ibm.fabric.service.FabcarService;

import io.swagger.annotations.ApiOperation;

@Slf4j
@RestController
@RequestMapping("/fabcar")
public class FabcarController {
	
	@Autowired
	FabcarService fabcarService;
	
	@ApiOperation(value = "getGreeting", nickname = "getGreeting")
	@GetMapping
	public String getHello() {
		Car car = new Car();
		
		
		return "Hello";
	}
	
	@ApiOperation(value = "cars", nickname = "cars search")
	@GetMapping("/cars")
	public String getAllCars() {
		return fabcarService.getAllCars();
	}
	
	@GetMapping("/cars/{carName}")
	public String getCar(@PathVariable String carName) {
		return fabcarService.getCar(carName);
	}
	
	@PostMapping("/cars")
	public String createCar(@RequestBody Car car) {
		return fabcarService.createCar(car);
	}
}
