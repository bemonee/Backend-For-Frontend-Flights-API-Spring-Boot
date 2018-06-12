package com.utn.tp5search.controllers;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.utn.tp5.dtos.AirportDTO;
import com.utn.tp5.dtos.RouteDTO;
import com.utn.tp5search.dtos.Airport;

@Controller
public class AirportController {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Environment env;

	@GetMapping("/airports/origin")
	public ResponseEntity<?> getOriginAirports() {
		String baseUrl = env.getProperty("tp5-api.url");
		String url = baseUrl + "routes";

		ResponseEntity<RouteDTO[]> responseEntity;
		try {
			responseEntity = restTemplate.getForEntity(url, RouteDTO[].class);
		} catch (RestClientException e) {
			return new ResponseEntity<>("El servicio no se encuentra disponible en este momento.",
					HttpStatus.SERVICE_UNAVAILABLE);
		}

		Set<Airport> airports = new HashSet<>();
		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			AirportDTO origin;
			String originName;
			for (RouteDTO routeDTO : responseEntity.getBody()) {
				origin = routeDTO.getOrigin();
				originName = origin.getCity().getName();
				originName = originName + " (" + origin.getIata() + ") - ";
				originName = originName + origin.getName() + " - ";
				originName = originName + origin.getCity().getState().getCountry().getName();
				airports.add(new Airport(originName, origin.getIata()));
			}
		}
		return new ResponseEntity<>(airports, HttpStatus.OK);
	}

	@GetMapping("/airports/{originIata}/destinations")
	public ResponseEntity<?> getDestinationAirports(@PathVariable("originIata") String iata) {
		String baseUrl = env.getProperty("tp5-api.url");
		String url = baseUrl + "/airports/" + iata + "/routes";

		ResponseEntity<AirportDTO[]> responseEntity;
		try {
			responseEntity = restTemplate.getForEntity(url, AirportDTO[].class);
		} catch (RestClientException e) {
			return new ResponseEntity<>("El servicio no se encuentra disponible en este momento.",
					HttpStatus.SERVICE_UNAVAILABLE);
		}
		
		Set<Airport> airports = new HashSet<>();
		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			String originName;
			for (AirportDTO airportDTO : responseEntity.getBody()) {
				originName = airportDTO.getCity().getName();
				originName = originName + " (" + airportDTO.getIata() + ") - ";
				originName = originName + airportDTO.getName() + " - ";
				originName = originName + airportDTO.getCity().getState().getCountry().getName();
				airports.add(new Airport(originName, airportDTO.getIata()));
			}
		}

		return new ResponseEntity<>(airports, HttpStatus.OK);
	}
}
