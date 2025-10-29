package com.scm.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scm.entities.SaloonService;
import com.scm.entities.ServiceImage;

public interface ServiceImageRepository extends JpaRepository<ServiceImage, Long> {

	List<ServiceImage> findAllImageBySaloonService(SaloonService saloonService);
}
