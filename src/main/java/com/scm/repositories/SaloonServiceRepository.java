package com.scm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scm.entities.SaloonService;

public interface SaloonServiceRepository extends JpaRepository<SaloonService, Long>{

	SaloonService getSaloonServiceByServiceName(String name);
	
}
