package com.test.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
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
	@Lazy
	LocationService service;
	private static final RestTemplate restTemplate = new RestTemplate();;
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final String apiKey = "";

	@CacheEvict(allEntries = true, cacheNames = { "geoCode", "chargingSpots", "restaurants", "petrol-stations" })
	@Scheduled(fixedRate = 20000)
	public void clearCache() {
	}

	public String getSpots(String cityName) throws InterruptedException, ExecutionException {
		List<Float> geoCode = service.fetchGeoCodesByCityName(cityName);
		CompletableFuture<List<Object>> nearbyRestaurants = service.getNearByRestaurants(geoCode);
		CompletableFuture<List<Object>> nearbyChargingStations = service.getNearByChargingSpots(geoCode);
		CompletableFuture<List<Object>> nearbyPetrolStations = service.getNearByPetrolStations(geoCode);
//		CompletableFuture.allOf(nearbyRestaurants, nearbyChargingStations, nearbyPetrolStations);
		System.out.println(nearbyRestaurants.get());
		System.out.println(nearbyChargingStations.get());
		System.out.println(nearbyPetrolStations.get());
		return nearbyRestaurants.toString() + "-------------------------------------"
				+ nearbyChargingStations.toString();
	}

	@Cacheable("geoCode")
	public List<Float> fetchGeoCodesByCityName(String cityName) {
		String url = "https://geocoder.ls.hereapi.com/6.2/geocode.json?searchtext=" + cityName + "&gen=9&apiKey="
				+ apiKey;
		JsonNode returnFromAPI = getResponseFromThirdPartyAPI(url);
		JsonNode geoCode = returnFromAPI.get("Response").get("View").get(0).get("Result").get(0).get("Location")
				.get("DisplayPosition");
		List<Float> response = new ArrayList<>();
		response.add(geoCode.get("Latitude").floatValue());
		response.add(geoCode.get("Longitude").floatValue());
		return response;
	}

	@Cacheable("chargingSpots")
	@Async
	public CompletableFuture<List<Object>> getNearByChargingSpots(List<Float> geoCode) {
		String url = "https://places.ls.hereapi.com/places/v1/discover/search?at=" + geoCode.get(0) + ","
				+ geoCode.get(1) + "&q=charging-stations&apiKey=" + apiKey;
		JsonNode returnFromAPI = getResponseFromThirdPartyAPI(url);
		List<Object> items = new ArrayList<>();
		returnFromAPI.get("results").get("items").forEach(item -> items.add(item.get("title")));
		try {
			System.out.println("2-sleeping-Cha");
			Thread.sleep(5000);
			System.out.println("2-DONE-sleeping-Cha");
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println(22222);
		return CompletableFuture.completedFuture(items);
	}

	@Cacheable("restaurants")
//	@Async
	public CompletableFuture<List<Object>> getNearByRestaurants(List<Float> geoCode) {
		String url = "https://places.ls.hereapi.com/places/v1/discover/search?at=" + geoCode.get(0) + ","
				+ geoCode.get(1) + "&q=restaurants&apiKey=" + apiKey;
		JsonNode returnFromAPI = getResponseFromThirdPartyAPI(url);
		List<Object> items = new ArrayList<>();
		returnFromAPI.get("results").get("items").forEach(item -> items.add(item.get("title")));
		try {
			System.out.println("1-Sleeping-Rest");
			Thread.sleep(10000);
			System.out.println("1-Done-sleeping-Rest");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println(1111);
		return CompletableFuture.completedFuture(items);
	}

	@Cacheable("petrol-stations")
//	@Async
	public CompletableFuture<List<Object>> getNearByPetrolStations(List<Float> geoCode) {
		String url = "https://places.ls.hereapi.com/places/v1/discover/search?at=" + geoCode.get(0) + ","
				+ geoCode.get(1) + "&q=petrol-stations&apiKey=" + apiKey;
		JsonNode returnFromAPI = getResponseFromThirdPartyAPI(url);
		List<Object> items = new ArrayList<>();
		returnFromAPI.get("results").get("items").forEach(item -> items.add(item.get("title")));
		System.err.println(333333);
		return CompletableFuture.completedFuture(items);
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