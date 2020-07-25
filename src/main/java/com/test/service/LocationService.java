package com.test.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LocationService {

	/*
	 * @Autowired CacheManager cache;
	 */
	@Autowired
	LocationService service;
	private static final RestTemplate restTemplate = new RestTemplate();;
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final String apiKey = "";

	@CacheEvict(allEntries = true, cacheNames = { "geoCode", "chargingSpots" })
	@Scheduled(fixedRate = 20000)
	public void clearCache() {
		System.out.println("clearing");
	}

	public String getSpots(String cityName) {
		List<Float> geoCode = service.fetchGeoCodesByCityName(cityName);
		List<Object> nearbyRestaurants = service.getNearByChargingSpots(geoCode);
		System.out.println(nearbyRestaurants);
		return "Hello";
	}

	@Cacheable("geoCode")
	public List<Float> fetchGeoCodesByCityName(String cityName) {
		String url = "https://geocoder.ls.hereapi.com/6.2/geocode.json?searchtext=" + cityName + "&gen=9&apiKey="
				+ apiKey;
		System.out.println("From method fetchGeoCodesByCityName");
		JsonNode returnFromAPI = getResponseFromThirdPartyAPI(url);
		JsonNode geoCode = returnFromAPI.get("Response").get("View").get(0).get("Result").get(0).get("Location")
				.get("DisplayPosition");
		List<Float> response = new ArrayList<>();
		response.add(geoCode.get("Latitude").floatValue());
		response.add(geoCode.get("Longitude").floatValue());
		return response;
	}

	@Cacheable("chargingSpots")
	public List<Object> getNearByChargingSpots(List<Float> geoCode) {
		String url = "https://places.ls.hereapi.com/places/v1/discover/search?at=" + geoCode.get(0) + ","
				+ geoCode.get(1) + "&q=restaurant&apiKey=" + apiKey;
		JsonNode returnFromAPI = getResponseFromThirdPartyAPI(url);
		List<Object> items = new ArrayList<>();
		returnFromAPI.get("result").get("items").forEach(item -> items.add(item));
//		String response = returnFromAPI.toString();
		return items;
	}

	private JsonNode getResponseFromThirdPartyAPI(String url) {
		String response = restTemplate.getForObject(url, String.class);
		System.out.println();
		JsonNode actualObj = null;
		try {
			actualObj = mapper.readTree(response);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return actualObj;
	}
}