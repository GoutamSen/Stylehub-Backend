package com.scm.repositories;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.scm.entities.AppointmentStatus;
import com.scm.entities.BookAppointment;
import com.scm.entities.User;

public interface BookAppointmentRepository extends JpaRepository<BookAppointment, Long> {


	 List<BookAppointment> findByDateBetween(LocalDate start, LocalDate end);
	 List<BookAppointment> findByDate(LocalDate date);
	 List<BookAppointment> getAllAppointmentsByUser(User user);
	 List<BookAppointment> getAllAppointmentByAppointmentStatus(AppointmentStatus status);
	 long countAppointmentByDate(LocalDate date);
	 long countAppointmentByDateBetween(LocalDate start , LocalDate end);
	 
}
