package com.utn.tp5search.controllers;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.utn.tp5.dtos.PriceDTO;
import com.utn.tp5search.dtos.Price;

@Controller
public class PriceController {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Environment env;

	@GetMapping("/prices/{originIata}/{destinationIata}/{fromDate}/{toDate}")
	public ResponseEntity<?> getPricesByRouteAndDates(@PathVariable("originIata") String originIata,
			@PathVariable("destinationIata") String destinationIata,
			@PathVariable("fromDate") @DateTimeFormat(iso = ISO.DATE) LocalDate fromDate,
			@PathVariable("toDate") @DateTimeFormat(iso = ISO.DATE) LocalDate toDate) {
		String baseUrl = env.getProperty("tp5-api.url");
		String url = baseUrl + "/prices/" + originIata + "/" + destinationIata + "/" + fromDate + "/" + toDate;

		ResponseEntity<PriceDTO[]> responseEntity;
		try {
			responseEntity = restTemplate.getForEntity(url, PriceDTO[].class);
		} catch (RestClientException e) {
			return new ResponseEntity<>("El servicio no se encuentra disponible en este momento.",
					HttpStatus.SERVICE_UNAVAILABLE);
		}
		
		Set<Price> prices = new HashSet<Price>();
		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			for(PriceDTO priceDTO : responseEntity.getBody()) {
				Price p = new Price();
				p.setCabin(priceDTO.getCabin().getName());
				p.setCabinId(priceDTO.getCabin().getId());
				p.setPrice(priceDTO.getPrice());
				prices.add(p);
			}
		}
		
		return new ResponseEntity<>(prices, HttpStatus.OK);
	}
}
