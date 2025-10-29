package com.scm.services;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import com.razorpay.Order;
import com.razorpay.RazorpayException;
import com.scm.entities.AppointmentStatus;
import com.scm.entities.BookAppointment;
import com.scm.requests.BookAppointmentRequest;
import com.scm.requests.UpdateAppointmentStatusRequest;

public interface BookAppointmentService {

	    BookAppointment bookAppointment(BookAppointmentRequest appointment);
	    List<BookAppointment> getAllAppointments();
	    BookAppointment getAppointmentById(Long id);
	    Optional<BookAppointment> getAppointmentByEmail(String email);
	    List<BookAppointment> getAllAppointmentByUsernameOrEmail(String usernameOrEmail);
	    List<BookAppointment> getAllAppointmentForDay();
	    List<BookAppointment> getAppointmentsForWeek();
	    List<BookAppointment> getAppointmentsForMonth();
	    List<BookAppointment> getAppointmentsForYear();
	    Long countAppointmentForDay();
	    Long countAppointmentForWeek();
	    Long countAppointmentForMonth();
	    Long countAppointmentForYear();
	    Double calculateDayRevenue();
	    Double calculateWeekRevenue();
	    Double calculateMonthlyRevenue();
	    Double calculateYearRevenue();
        String updateAppointmentStatus(UpdateAppointmentStatusRequest request);
        String buildEmailMessage(BookAppointment bookAppointment,AppointmentStatus newStatus);
        
        Order initiatePayment(Long appointmentId,Double requestedAmount) throws RazorpayException;
        Boolean verifyPayment(Long appointmentId,String paymentId,String orderId,String signature);
}
