package com.scm.servicesimpl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.scm.entities.SaloonService;
import com.scm.entities.ServiceImage;
import com.scm.exception.ResourceNotFoundException;
import com.scm.repositories.SaloonServiceRepository;
import com.scm.repositories.ServiceImageRepository;
import com.scm.services.ServiceImageService;

@Service
public class ServiceImageServiceImpl implements ServiceImageService {

	@Autowired
	private ServiceImageRepository imageRepository;
	@Autowired
	private SaloonServiceRepository saloonServiceRepository;

	@Override
	public ServiceImage addServiceImage(ServiceImage serviceImage) {
		return imageRepository.save(serviceImage);
	}

	@Override
	public ServiceImage getServiceImage(Long id) {
		ServiceImage image = imageRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Image Does Not Exist For This Id : "));
		return image;
	}

	@Override
	public List<ServiceImage> getServiceImage() {
		return imageRepository.findAll();
	}

	@Override
	public List<ServiceImage> getImagesBySaloonService(Long serviceId) {
		SaloonService saloonService = saloonServiceRepository.findById(serviceId)
				.orElseThrow(() -> new ResourceNotFoundException("Saloon Service Does Not Exist For This Id :"));
		return imageRepository.findAllImageBySaloonService(saloonService);
	}

	@Override
	public List<ServiceImage> getImageBySaloonService(String serviceName) {
		
		SaloonService saloonService = saloonServiceRepository.getSaloonServiceByServiceName(serviceName);
		return imageRepository.findAllImageBySaloonService(saloonService);
	}

}
