package com.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.domain.Student;

@Service
public class StudentService {

	@Cacheable("student")
	public Student getStudentByID(String id) {
		try {
			System.out.println("Going to sleep for 5 Secs.. to simulate backend call.");
			Thread.sleep(1000 * 5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return new Student(id, "Sajal", "V");
	}
	
	@CacheEvict(value = "student", allEntries = true)
	@Scheduled(fixedRate = 20000)
	public void clearCache() {
		System.out.println("clearing");
	}

	public String getHereAPIresponse() {
		String url = "https://places.ls.hereapi.com/places/v1/discover/explore?at=52.5159,13.3777&apiKey=";

		String j = new RestTemplate().getForObject(url, String.class);
		System.out.println(j);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode actualObj = null;
		try {
			actualObj = mapper.readTree(j);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (actualObj != null)
			System.out.println(actualObj.get("search").get("context").get("location").get("position"));
		return j;

	}
}