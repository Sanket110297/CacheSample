package com.test.controller;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.test.service.LocationService;

@RestController
public class LocationController {

	@Autowired
	@Lazy
	private LocationService locationService;

	@GetMapping("/fetch/{cityName}")
	public String getHereAPIresponse(@PathVariable String cityName) {
		try {
			return locationService.getSpots(cityName);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cityName;

	}
}