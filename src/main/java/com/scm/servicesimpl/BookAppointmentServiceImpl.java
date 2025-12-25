package com.scm.servicesimpl;

import java.io.IOException;
import java.security.Principal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.razorpay.Order;
import com.razorpay.RazorpayException;
import com.scm.entities.AppointmentStatus;
import com.scm.entities.BookAppointment;
import com.scm.entities.PaymentStatus;
import com.scm.entities.SaloonService;
import com.scm.entities.ServiceImage;
import com.scm.entities.User;
import com.scm.exception.ResourceNotFoundException;
import com.scm.exception.UserNotFoundException;
import com.scm.repositories.BookAppointmentRepository;
import com.scm.repositories.SaloonServiceRepository;
import com.scm.repositories.ServiceImageRepository;
import com.scm.repositories.UserRepository;
import com.scm.requests.BookAppointmentRequest;
import com.scm.requests.UpdateAppointmentStatusRequest;
import com.scm.services.BookAppointmentService;
import com.scm.services.PaymentService;
import com.scm.services.UserService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;


@Service
public class BookAppointmentServiceImpl implements BookAppointmentService {

	@Autowired
	private BookAppointmentRepository appointmentRepo;
	@Autowired
	private SaloonServiceRepository serviceRepo;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ServiceImageRepository serviceImageRepository;
	@Autowired
	private UserService userService;

	@Override
	public BookAppointment bookAppointment(BookAppointmentRequest appointmentRequest) {
	    User user = userRepository.findByEmail(appointmentRequest.getEmail())
	            .orElseThrow(() -> new UserNotFoundException("User Does Not Exist For This Email : "));

	    List<ServiceImage> serviceImages = serviceImageRepository.findAllById(appointmentRequest.getServiceImageIds());

	    BookAppointment appointment = new BookAppointment();
	    appointment.setUser(user);
	    appointment.setDate(appointmentRequest.getDate());
	    appointment.setTime(appointmentRequest.getTime());
	    appointment.setNotes(appointmentRequest.getNotes());

	    appointment.setAppointmentStatus(AppointmentStatus.PENDING);
	    appointment.setAmountPaid(0.0);
	    appointment.setPaymentStatus(PaymentStatus.UNPAID);

	    double totalAmount = serviceImages.stream()
	            .mapToDouble(ServiceImage::getPrice)
	            .sum();
	    appointment.setTotalAmount(totalAmount);
	    appointment.setAmountPaid(0.0);
	    appointment.setRemainingAmount(totalAmount);
	    appointment.setServiceImages(serviceImages);
	    BookAppointment savedAppointment = appointmentRepo.save(appointment);
	    
	    // --- Send Email to Admin after booking ---
//	    try {
//	        MimeMessage mimeMessage = mailSender.createMimeMessage();
//	        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//
//	        // Send to admin’s email (configure this in properties or keep a constant)
//	        String adminEmail = "sengoutam689@gmail.com";  
//	        helper.setTo(adminEmail);
//	        helper.setSubject("New Appointment Booked - StyleHub");
//
//	        String emailBody = buildAdminEmailMessage(savedAppointment);
//	        helper.setText(emailBody, true);
//
//	        mailSender.send(mimeMessage);
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        throw new RuntimeException("Failed to send email to admin");
//	    }
    
	    
	    Email from = new Email("sengoutam689@gmail.com");  
	    Email to = new Email("sengoutam6890@gmail.com");   // admin email address
	    String messageBody = buildAdminEmailMessage(savedAppointment);
	    Content content = new Content("text/html", messageBody);
	    String subject = "Cusomter Request for the appointment";
	    Mail mail = new Mail(from, subject, to, content);
	    SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
	    try {
	    	Request request = new Request();	    
	    	request.setMethod(Method.POST);
	    	request.setEndpoint("mail/send");
			request.setBody(mail.build());
			Response response = sg.api(request);
			System.out.println("Email sent to the admin with status : "+response.getStatusCode());
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    return savedAppointment;
	}

	// Build email content for admin notification
	private String buildAdminEmailMessage(BookAppointment appointment) {
	    String customerName = appointment.getUser().getUsername();
	    String customerEmail = appointment.getUser().getEmail();
	    String appointmentTime = appointment.getDate() + " at " + appointment.getTime();

	    StringBuilder servicesList = new StringBuilder();
	    for (ServiceImage service : appointment.getServiceImages()) {
	        servicesList.append("- ").append(service.getStyleName())
	                .append(" (₹").append(service.getPrice()).append(")<br>");
	    }

	    // URLs for confirm/cancel (Option 1: backend GET endpoints)
	    String confirmUrl = "https://stylehub-1-degl.onrender.com/appointments/" + appointment.getId() + "/confirm";
	    String cancelUrl  = "https://stylehub-1-degl.onrender.com/appointments/" + appointment.getId() + "/cancel";

	    return "<p>Hello Admin,</p>"
	            + "<p>A new appointment has been booked:</p>"
	            + "<p><b>Customer Name:</b> " + customerName + "<br>"
	            + "<b>Email:</b> " + customerEmail + "<br>"
	            + "<b>Date & Time:</b> " + appointmentTime + "<br>"
	            + "<b>Total Amount:</b> ₹" + appointment.getTotalAmount() + "</p>"
	            + "<p><b>Services:</b><br>" + servicesList.toString() + "</p>"

	            // ✅ Confirm button
	            + "<p><a href='" + confirmUrl + "' "
	            + "style='background-color:green; color:white; padding:10px 15px; margin: 2px;"
	            + "text-decoration:none; border-radius:5px;'>✅ Confirm</a></p>"

	            // ❌ Cancel button
	            + "<br>"
	            + "<p><a href='" + cancelUrl + "' "
	            + "style='background-color:red; color:white; padding:10px 15px; "
	            + "text-decoration:none; border-radius:5px;'>❌ Cancel</a></p>"

	            + "<br><p>Regards,<br>StyleHub System</p>";
	}


	@Override
	public List<BookAppointment> getAllAppointments() {
		return appointmentRepo.findAll();
	}

	@Override
	public BookAppointment getAppointmentById(Long id) {
		return appointmentRepo.findById(id).orElseThrow(() -> new RuntimeException("Appointment not found"));
	}


	@Override
	public List<BookAppointment> getAppointmentsForWeek() {
		// TODO Auto-generated method stub
		LocalDate startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
		LocalDate endOfWeek = LocalDate.now().with(DayOfWeek.SUNDAY);
		return appointmentRepo.findByDateBetween(startOfWeek, endOfWeek);
	}

	@Override
	public List<BookAppointment> getAppointmentsForMonth() {
		// TODO Auto-generated method stub
		LocalDate firstDay = LocalDate.now().withDayOfMonth(1);
		LocalDate lastDay = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
		return appointmentRepo.findByDateBetween(firstDay, lastDay);
	}

	@Override
	public List<BookAppointment> getAppointmentsForYear() {
		// TODO Auto-generated method stub
		LocalDate firstDay = LocalDate.now().withDayOfYear(1);
		LocalDate lastDay = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
		return appointmentRepo.findByDateBetween(firstDay, lastDay);
	}

	@Override
	public List<BookAppointment> getAllAppointmentForDay() {
		List<BookAppointment> listOfAppointments = appointmentRepo.findByDate(LocalDate.now());
		System.out.println("listOfAppointments" + listOfAppointments);
		return listOfAppointments;
	}

	@Override
	public Long countAppointmentForDay() {
		return appointmentRepo.countAppointmentByDate(LocalDate.now());
	}

	@Override
	public Long countAppointmentForWeek() {
		LocalDate startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
		LocalDate endOfWeek = LocalDate.now().with(DayOfWeek.SUNDAY);
		return appointmentRepo.countAppointmentByDateBetween(startOfWeek, endOfWeek);
	}

	@Override
	public Long countAppointmentForMonth() {
		LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
		LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
		return appointmentRepo.countAppointmentByDateBetween(startOfMonth, endOfMonth);
	}

	@Override
	public Long countAppointmentForYear() {
		LocalDate firstDay = LocalDate.now().withDayOfYear(1);
		LocalDate lastDay = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
		return appointmentRepo.countAppointmentByDateBetween(firstDay, lastDay);
	}

	@Override
	public Double calculateDayRevenue() {
		List<BookAppointment> listOfAppointments = appointmentRepo.findByDate(LocalDate.now());
		// sum up prices of all services
		return listOfAppointments.stream().mapToDouble(a->a.getAmountPaid()!= null ? a.getAmountPaid():0.0).sum();
	}

	@Override
	public Double calculateWeekRevenue() {
		LocalDate startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
		LocalDate endOfWeek = LocalDate.now().with(DayOfWeek.SUNDAY);
		List<BookAppointment> listOfAppointment = appointmentRepo.findByDateBetween(startOfWeek, endOfWeek);
		 return listOfAppointment.stream()
		            .mapToDouble(a -> a.getAmountPaid() != null ? a.getAmountPaid() : 0.0)
		            .sum();
	}

	@Override
	public Double calculateMonthlyRevenue() {
		LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
		LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
		List<BookAppointment> listOfAppointment = appointmentRepo.findByDateBetween(startOfMonth, endOfMonth);
		return listOfAppointment.stream().mapToDouble(a->a.getAmountPaid()!=null?a.getAmountPaid():0.0).sum();
	}

	@Override
	public Double calculateYearRevenue() {
		LocalDate startOfYear = LocalDate.now().withDayOfYear(1);
		LocalDate endOfyear = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
		List<BookAppointment> listOfAppointment = appointmentRepo.findByDateBetween(startOfYear, endOfyear);
		return listOfAppointment.stream().mapToDouble(a->a.getAmountPaid()!=null?a.getAmountPaid():0.0).sum();
	}


	@Override
	public String updateAppointmentStatus(UpdateAppointmentStatusRequest request) {
		System.out.println("start update appointment status method ");
		BookAppointment appointment = appointmentRepo.findById(request.getId())
				.orElseThrow(() -> new ResourceNotFoundException("No appointment for this Id : " + request.getId()));

		if (appointment.getAppointmentStatus() == request.getNewStatus()) {
			return "Appointment Status is already " + appointment.getAppointmentStatus();
		}

		// Update fields
		appointment.setAppointmentStatus(request.getNewStatus());
		appointment.setPaymentStatus(PaymentStatus.UNPAID);
		appointment.setConfirmationTime(LocalDateTime.now());
		appointment.setRemainingAmount(appointment.getTotalAmount());
	    appointmentRepo.save(appointment);
		System.out.println("saved appointment -> "+appointment);
		
		Email from = new Email("sengoutam689@gmail.com");  
	    Email to = new Email("sengoutam6890@gmail.com");   // admin email address
	    String messageBody = buildEmailMessage(appointment,request.getNewStatus());
	    Content content = new Content("text/html", messageBody);
	    String subject = "Admin update the status of an appointment";
	    Mail mail = new Mail(from, subject, to, content);
	    SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
	    try {
	    	System.out.println("enter into try block");
	    	Request req = new Request();	    
	    	req.setMethod(Method.POST);
	    	req.setEndpoint("mail/send");
	    	req.setBody(mail.build());
			Response response = sg.api(req);
			System.out.println("Email sent to the customer with status : "+response.getStatusCode());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "Appointment status updated and email sent.";
	}

	@Override
	public String buildEmailMessage(BookAppointment appointment, AppointmentStatus newStatus) {
		String customerName = appointment.getUser().getUsername();
		String appointmentTime = appointment.getDate() + " at " + appointment.getTime();
		String paymentLink = "https://stylehub-apps.netlify.app/payment";

		switch (newStatus) {
		case CONFIRMED:
			return "<p>Hello " + customerName + ",</p>"
					+ "<p>Great news! Your appointment has been <b>CONFIRMED</b>.</p>"
					+ "<p>📅 <b>Appointment Details:</b><br>" + "- Date & Time: " + appointmentTime + "</p>"
					+ "<p>💰 <b>Payment Instructions:</b><br>"
					+ "- You must pay at least <b>30%</b> of the total amount within 2 hours.<br>"
					+ "- If you pay <b>100%</b> within 2 hours, you will receive a <b>10% discount</b>.<br>"
					+ "- If no payment is made within 2 hours, your appointment will be automatically <b>CANCELLED</b>.</p>"
					+ "<p>👉 <a href='" + paymentLink + appointment.getId()
					+ "' style='color:blue; font-weight:bold;' target='_blank'>Click here to pay now</a></p>"
					+ "<br><p>Thank you for choosing <b>StyleHub</b>!</p>" + "<p>Best regards,<br>StyleHub Team</p>";
		case CANCELLED:
			return "<p>Hello " + customerName + ",</p>"
					+ "<p>We are sorry to inform you that your appointment has been <b>CANCELLED</b>.</p>"
					+ "<p>If you have any questions, feel free to contact us.</p>"
					+ "<br><p>Regards,<br>StyleHub Team</p>";

		case COMPLETED:
			return "<p>Hello " + customerName + ",</p>" + "<p>Your appointment on <b>" + appointmentTime
					+ "</b> has been successfully <b>COMPLETED</b>.</p>"
					+ "<p>We hope you had a great experience with us!</p>"
					+ "<br><p>Thank you for visiting <b>StyleHub</b>.</p>" + "<p>Regards,<br>StyleHub Team</p>";

		case PENDING:
			return "<p>Hello " + customerName + ",</p>"
					+ "<p>Your appointment is currently in <b>PENDING</b> status.</p>"
					+ "<p>We will notify you once it is confirmed.</p>" + "<br><p>Regards,<br>StyleHub Team</p>";

		default:
			return "<p>Hello " + customerName + ",</p>" + "<p>Your appointment status has been updated to: <b>"
					+ newStatus + "</b>.</p>" + "<br><p>Thank you,<br>StyleHub Team</p>";
		}
	}

	@Scheduled(fixedRate = 60000) // runs every 1 min
	public void autoCancelUnpaid() {
		List<BookAppointment> confirmed = appointmentRepo
				.getAllAppointmentByAppointmentStatus(AppointmentStatus.CONFIRMED);

		for (BookAppointment appt : confirmed) {
			if (appt.getConfirmationTime() == null)
				continue; // safety check

			Duration diff = Duration.between(appt.getConfirmationTime(), LocalDateTime.now());

			if (diff.toHours() >= 2 && appt.getAmountPaid() < (appt.getTotalAmount() * 0.3)) {
				appt.setAppointmentStatus(AppointmentStatus.CANCELLED);
				appt.setPaymentStatus(PaymentStatus.FAIL);
				appointmentRepo.save(appt);
			}
		}
	}

	// Create Razorpay order (based on user input)
	@Override
	public Order initiatePayment(Long appointmentId, Double requestedAmount) throws RazorpayException {
		BookAppointment appt = appointmentRepo.findById(appointmentId)
				.orElseThrow(() -> new RuntimeException("Appointment not found"));

		double minAdvance = appt.getTotalAmount() * 0.3;

		if (requestedAmount < minAdvance) {
			throw new IllegalArgumentException("You must pay at least 30% (₹" + minAdvance + ")");
		}

		Order order = paymentService.createRazorpayOrder(requestedAmount);
		appt.setRazorpayOrderId(order.get("id"));
		appointmentRepo.save(appt);

		return order;
	}

	@Override
	public Boolean verifyPayment(Long appointmentId, String paymentId, String orderId, String signature) {
		BookAppointment appt = appointmentRepo.findById(appointmentId)
				.orElseThrow(() -> new RuntimeException("Appointment not found"));

		// TODO: Verify signature using Razorpay secret key

		LocalDateTime now = LocalDateTime.now();
		Duration diff = Duration.between(appt.getConfirmationTime(), now);
		boolean withinTwoHours = diff.toHours() < 2;

		double discount = 0.0;

		// Full payment with discount
		if (withinTwoHours && appt.getRemainingAmount().equals(appt.getTotalAmount())) {
			if (appt.getTotalAmount().equals(appt.getAmountPaid() + appt.getRemainingAmount())) {
				discount = appt.getTotalAmount() * 0.1;
				appt.setTotalAmount(appt.getTotalAmount() - discount);
			}
		}

		appt.setAmountPaid(appt.getAmountPaid() + (appt.getTotalAmount() - discount));
		appt.setRemainingAmount(appt.getTotalAmount() - appt.getAmountPaid());
		appt.setPaymentStatus(appt.getRemainingAmount() <= 0 ? PaymentStatus.FULLY_PAID : PaymentStatus.PARTIALLY_PAID);
		appointmentRepo.save(appt);
		return true;
	}

	@Override
	public Optional<BookAppointment> getAppointmentByEmail(String email) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public List<BookAppointment> getAllAppointmentByUsernameOrEmail(String usernameOrEmail) {
		User user = userService.findByUsernameOrEmail(usernameOrEmail);
		List<BookAppointment> listOfBookAppointments  =  appointmentRepo.getAllAppointmentsByUser(user);
		return listOfBookAppointments;
	}

}
