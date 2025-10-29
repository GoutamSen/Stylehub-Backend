package com.scm.services;

import java.util.List;

import com.scm.entities.SaloonService;

public interface SaloonServiceService {

	SaloonService addSaloonService(SaloonService saloonService);
	SaloonService getSaloonService(Long saloonServiceId);
	List<SaloonService> getAllSaloonService();
}
