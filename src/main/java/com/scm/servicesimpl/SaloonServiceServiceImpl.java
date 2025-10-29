package com.scm.servicesimpl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.scm.entities.SaloonService;
import com.scm.exception.ResourceNotFoundException;
import com.scm.repositories.SaloonServiceRepository;
import com.scm.services.SaloonServiceService;

@Service
public class SaloonServiceServiceImpl implements SaloonServiceService {

	@Autowired
	private SaloonServiceRepository saloonServiceRepository;
	
	@Override
	public SaloonService addSaloonService(SaloonService saloonService) {
		SaloonService savedSaloonService = saloonServiceRepository.save(saloonService);
		return savedSaloonService;
	}

	@Override
	public SaloonService getSaloonService(Long saloonServiceId) {
		return saloonServiceRepository.findById(saloonServiceId).orElseThrow(()-> new ResourceNotFoundException("SaloonService Does Not Exist for this Id : "));
	}

	@Override
	public List<SaloonService> getAllSaloonService() {
		return saloonServiceRepository.findAll();
	}

}
