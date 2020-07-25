package com.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.test.service.LocationService;

@RestController
public class LocationController {

	@Autowired
	private LocationService locationService;

	@GetMapping("/fetch/{cityName}")
	public String getHereAPIresponse(@PathVariable String cityName) {
		return locationService.getSpots(cityName);

	}
}