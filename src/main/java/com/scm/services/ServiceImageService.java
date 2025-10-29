
package com.scm.services;

import java.util.List;
import com.scm.entities.ServiceImage;

public interface ServiceImageService {

	  ServiceImage addServiceImage(ServiceImage serviceImage);
	  ServiceImage getServiceImage(Long id);
	  List<ServiceImage> getImagesBySaloonService(Long serviceId);
	  List<ServiceImage> getServiceImage();
	  List<ServiceImage> getImageBySaloonService(String serviceName);
}
