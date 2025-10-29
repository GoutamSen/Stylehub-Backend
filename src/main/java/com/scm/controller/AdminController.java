package com.scm.controller;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scm.entities.AppointmentStatus;
import com.scm.entities.BookAppointment;
import com.scm.entities.Role;
import com.scm.entities.Status;
import com.scm.entities.User;
import com.scm.requests.UpdateAppointmentStatusRequest;
import com.scm.requests.UpdateStatusRequest;
import com.scm.response.ApiResponse;
import com.scm.services.BookAppointmentService;
import com.scm.services.UserService;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private BookAppointmentService appointmentService;
	@Autowired
	private UserService userService;

	@GetMapping("/dashboard")
	public String adminDashboard() {
		return "Admin Dashboard Accessed";
	}

	@GetMapping("/allappointments")
	public ResponseEntity<ApiResponse<List<BookAppointment>>> getAppointmentsByFilter(@RequestParam String filter) {
		List<BookAppointment> appointments;
		switch (filter.toLowerCase().trim()) {
		case "day":
			appointments = appointmentService.getAllAppointmentForDay();
			break;
		case "week":
			appointments = appointmentService.getAppointmentsForWeek();
			break;
		case "month":
			appointments = appointmentService.getAppointmentsForMonth();
			break;
		case "year":
			appointments = appointmentService.getAppointmentsForYear();
			break;
		default:
			appointments = appointmentService.getAppointmentsForMonth();
			break;
		}
		ApiResponse<List<BookAppointment>> response = ApiResponse.<List<BookAppointment>>builder()
				.timestamp(LocalDateTime.now()).status(HttpStatus.OK.value())
				.message("Appointments fetched successfully for filter: " + filter).data(appointments).build();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/appointments/counts")
	public ResponseEntity<ApiResponse<Long>> countAppointmentsByFilter(@RequestParam String filter) {
	    System.out.println("start count appointment");
		long count;
		String normalized = filter.toLowerCase().trim();

		switch (normalized) {
		case "day":
			count = appointmentService.countAppointmentForDay();
			break;
		case "week":
			count = appointmentService.countAppointmentForWeek();
			break;
		case "month":
			count = appointmentService.countAppointmentForMonth();
			break;
		case "year":
			count = appointmentService.countAppointmentForYear();
			break;
		default:
			ApiResponse<Long> errorResponse = ApiResponse.<Long>builder().timestamp(LocalDateTime.now())
					.status(HttpStatus.BAD_REQUEST.value())
					.message("❌ Invalid filter: " + filter + ". Allowed values: day, week, month, year").data(null)
					.build();
			return ResponseEntity.badRequest().body(errorResponse);
		}

		ApiResponse<Long> response = ApiResponse.<Long>builder().timestamp(LocalDateTime.now())
				.status(HttpStatus.OK.value())
				.message("✅ Appointments count fetched successfully for filter: " + filter).data(count).build();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/revenue")
	public ResponseEntity<ApiResponse<Double>> totalRevenue(@RequestParam String filter) {
		Double revenue = 0.0;
		Long userCounts = userService.coutOfUser();
		System.out.println("Count Of Users -> " + userCounts);
		String normalized = filter.toLowerCase().trim();

		switch (normalized) {
		case "day":
			revenue = appointmentService.calculateDayRevenue();
			break;
		case "week":
			revenue = appointmentService.calculateWeekRevenue();
			break;
		case "month":
			revenue = appointmentService.calculateMonthlyRevenue();
			break;
		case "year":
			revenue = appointmentService.calculateYearRevenue();
			break;
		default:
			ApiResponse<Double> errorResponse = ApiResponse.<Double>builder().timestamp(LocalDateTime.now())
					.status(HttpStatus.BAD_REQUEST.value())
					.message("❌ Invalid filter: " + filter + ". Allowed values: day, week, month, year").data(null)
					.build();
			return ResponseEntity.badRequest().body(errorResponse);
		}

		ApiResponse<Double> response = ApiResponse.<Double>builder().timestamp(LocalDateTime.now())
				.status(HttpStatus.OK.value()).message("✅ Revenue calculated successfully for filter: " + filter)
				.data(revenue).build();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/users/count")
	public ResponseEntity<ApiResponse<Long>> getUserCount() {
		Long count = userService.coutOfUser();
		ApiResponse<Long> response = ApiResponse.<Long>builder().timestamp(LocalDateTime.now())
				.message("Count of Users").data(count).status(HttpStatus.OK.value()).build();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/user")
	public ResponseEntity<ApiResponse<User>> findByUsernameOrEmail(@RequestParam String UsernameOrEmail) {
		System.out.println("start emthod ");
		User user = userService.findByUsernameOrEmail(UsernameOrEmail);
		ApiResponse<User> response = ApiResponse.<User>builder().timestamp(LocalDateTime.now())
				.status(HttpStatus.OK.value()).message("Find User By" + UsernameOrEmail).data(user).build();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/users/list")
	public ResponseEntity<ApiResponse<List<User>>> getAllUsers(@RequestParam Role role) {
		List<User> listOfUsers = userService.findUsersByRole(role);
		ApiResponse<List<User>> response = ApiResponse.<List<User>>builder().timestamp(LocalDateTime.now())
				.status(HttpStatus.OK.value()).message("Get All Users").data(listOfUsers).build();
		return ResponseEntity.ok(response);
	}

	@PutMapping("/user/status")
	public ResponseEntity<ApiResponse<String>> updateStatus(@RequestBody UpdateStatusRequest request) {
		String isUpdated = userService.updateStatus(request.getEmail(), request.getNewStatus());
		System.out.println("call the update status method ");
		System.out.println("status ->" + request.getNewStatus());
		ApiResponse<String> response = ApiResponse.<String>builder().timestamp(LocalDateTime.now())
				.status(HttpStatus.OK.value()).message(" User Status Updated to" + request.getNewStatus())
				.data(isUpdated).build();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/users/list/status")
	public ResponseEntity<ApiResponse<List<User>>> getAllUsersByStatus(@RequestParam Status status) {
		List<User> listOfUsers = userService.findUserByStatus(status);
		ApiResponse<List<User>> response = ApiResponse.<List<User>>builder().timestamp(LocalDateTime.now())
				.status(HttpStatus.OK.value()).message("Get All Users By Status").data(listOfUsers).build();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/count")
	public ResponseEntity<ApiResponse<Long>> countUsersByRole(@RequestParam Role role) {
		Long count = userService.countOfuserByRole(role);
		ApiResponse<Long> response = ApiResponse.<Long>builder().timestamp(LocalDateTime.now())
				.status(HttpStatus.OK.value()).message("Count All " + role).data(count).build();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/users/count/active")
	public ResponseEntity<ApiResponse<Long>> countUsersByStatus(@RequestParam Status status) {
		Long count = userService.countOfUserByStatus(status);
		ApiResponse<Long> response = ApiResponse.<Long>builder().timestamp(LocalDateTime.now())
				.status(HttpStatus.OK.value()).message("Count All " + status).data(count).build();
		return ResponseEntity.ok(response);
	}

	@PutMapping("/appointments/status")
	public ResponseEntity<ApiResponse<Void>> updateAppointmentStatus(
			@RequestBody UpdateAppointmentStatusRequest request) {
		System.out.println("start updateappointmentstatus");
		appointmentService.updateAppointmentStatus(request);
		ApiResponse<Void> response = ApiResponse.<Void>builder().timestamp(LocalDateTime.now())
				.status(HttpStatus.OK.value()).message("Appointment status updated to " + request.getNewStatus())
				.build();

		return ResponseEntity.ok(response);
	}

	
	
	@GetMapping("/appointments")
	public ResponseEntity<ApiResponse<List<BookAppointment>>> getAllAppointments() {
		List<BookAppointment> appointments = appointmentService.getAllAppointments();

		for(BookAppointment appointment : appointments) {
			System.out.println(appointment.getUser());
		}
		ApiResponse<List<BookAppointment>> response = ApiResponse.<List<BookAppointment>>builder()
				.timestamp(LocalDateTime.now()).status(HttpStatus.OK.value())
				.message("Fetched all appointments successfully").data(appointments).build();

		return ResponseEntity.ok(response);
	}
	
	
	@GetMapping("/appointments/usernameoremail")
	public ResponseEntity<ApiResponse<List<BookAppointment>>> getAllAppointmentByEmail(@RequestParam String usernameOrEmail){
		List<BookAppointment> listOfAppointments = appointmentService.getAllAppointmentByUsernameOrEmail(usernameOrEmail);
	    System.out.println("listOfAppointments --->"+listOfAppointments);
		ApiResponse<List<BookAppointment>> response = ApiResponse.<List<BookAppointment>>builder()
				                                      .timestamp(LocalDateTime.now())
				                                      .status(HttpStatus.OK.value())
				                                      .message("Find All Appointments By Email")
				                                      .data(listOfAppointments)
				                                      .build();
		return ResponseEntity.ok(response);
	}
	

}