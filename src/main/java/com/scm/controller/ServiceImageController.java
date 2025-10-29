package com.scm.controller;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.scm.entities.ServiceImage;
import com.scm.response.ApiResponse;
import com.scm.services.ServiceImageService;

@RestController
@RequestMapping("/api/service/images")
public class ServiceImageController {

	@Autowired private ServiceImageService imageServcie;
	
	@PostMapping("/add")
	public ResponseEntity<ApiResponse<ServiceImage>> addServiceImage(@RequestBody ServiceImage serviceImage){
		ServiceImage sercieImage = imageServcie.addServiceImage(serviceImage);
		ApiResponse<ServiceImage> response = ApiResponse.<ServiceImage>builder()
				                             .timestamp(LocalDateTime.now())
				                             .status(HttpStatus.OK.value())
				                             .message("Add Service Image Successfully : ")
				                             .data(sercieImage)
				                             .build();
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("")
	public ResponseEntity<ApiResponse<ServiceImage>> getServiceImage(@RequestParam Long id){
		ServiceImage sercieImage = imageServcie.getServiceImage(id);
		System.out.println(sercieImage);
		ApiResponse<ServiceImage> response = ApiResponse.<ServiceImage>builder()
				                             .timestamp(LocalDateTime.now())
				                             .status(HttpStatus.OK.value())
				                             .message("Fetch Service Image By Id : ")
				                             .data(sercieImage)
				                             .build();
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/all")
	public ResponseEntity<ApiResponse<List<ServiceImage>>> getAllServiceImage(){
		List<ServiceImage> sercieImage = imageServcie.getServiceImage();
		System.out.println(sercieImage);
		ApiResponse<List<ServiceImage>> response = ApiResponse.<List<ServiceImage>>builder()
				                             .timestamp(LocalDateTime.now())
				                             .status(HttpStatus.OK.value())
				                             .message("Fetch Service Image By Id : ")
				                             .data(sercieImage)
				                             .build();
		return ResponseEntity.ok(response);
	}
	
	
	@GetMapping("/all/service")
	public ResponseEntity<ApiResponse<List<ServiceImage>>> getAllServiceImage(@RequestParam Long serviceId){
		List<ServiceImage> sercieImage = imageServcie.getImagesBySaloonService(serviceId);
		System.out.println(sercieImage);
		ApiResponse<List<ServiceImage>> response = ApiResponse.<List<ServiceImage>>builder()
				                             .timestamp(LocalDateTime.now())
				                             .status(HttpStatus.OK.value())
				                             .message("Fetch Service Image By Saloon Service Id : ")
				                             .data(sercieImage)
				                             .build();
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/all/service/name")
	public ResponseEntity<ApiResponse<List<ServiceImage>>> getAllServiceImage(@RequestParam String serviceName){
		System.out.println("ServiceType ->"+serviceName);
		List<ServiceImage> sercieImage = imageServcie.getImageBySaloonService(serviceName);
		System.out.println(sercieImage);
		ApiResponse<List<ServiceImage>> response = ApiResponse.<List<ServiceImage>>builder()
				                             .timestamp(LocalDateTime.now())
				                             .status(HttpStatus.OK.value())
				                             .message("Fetch Service Image By Saloon Service Name : ")
				                             .data(sercieImage)
				                             .build();
		return ResponseEntity.ok(response);
	}
	
}
