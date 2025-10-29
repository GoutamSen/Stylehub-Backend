package com.scm.controller;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.scm.entities.SaloonService;
import com.scm.response.ApiResponse;
import com.scm.services.SaloonServiceService;

@RestController
@RequestMapping("/saloonservice")
public class SaloonServiceController {

	@Autowired
	private SaloonServiceService saloonServiceService;

	@PostMapping("/add")
	public ResponseEntity<ApiResponse<SaloonService>> addSaloonService(@RequestBody SaloonService saloonService) {
		SaloonService saved = saloonServiceService.addSaloonService(saloonService);

		ApiResponse<SaloonService> response = ApiResponse.<SaloonService>builder().status(HttpStatus.CREATED.value())
				.message("Saloon service added successfully").timestamp(LocalDateTime.now()).data(saved).build();

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<ApiResponse<SaloonService>> getSaloonService(@PathVariable Long id) {
	    SaloonService service = saloonServiceService.getSaloonService(id);

	    ApiResponse<SaloonService> response = ApiResponse.<SaloonService>builder()
	            .status(HttpStatus.OK.value())
	            .message("Saloon service fetched successfully")
	            .timestamp(LocalDateTime.now())
	            .data(service)
	            .build();

	    return ResponseEntity.ok(response);
	}

	@GetMapping("/getAll")
	public ResponseEntity<ApiResponse<List<SaloonService>>> getAllSaloonServices() {
	    List<SaloonService> services = saloonServiceService.getAllSaloonService();

	    ApiResponse<List<SaloonService>> response = ApiResponse.<List<SaloonService>>builder()
	            .status(HttpStatus.OK.value())
	            .message("All saloon services fetched successfully")
	            .timestamp(LocalDateTime.now())
	            .data(services)
	            .build();

	    return ResponseEntity.ok(response);
	}

}
