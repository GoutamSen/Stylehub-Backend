package com.scm.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.scm.entities.AppointmentStatus;
import com.scm.entities.BookAppointment;
import com.scm.requests.BookAppointmentRequest;
import com.scm.requests.UpdateAppointmentStatusRequest;
import com.scm.response.ApiResponse;
import com.scm.services.BookAppointmentService;

@RestController
@RequestMapping("/appointments")
@CrossOrigin(origins = "https://stylehub-apps.netlify.app", allowedHeaders = "*", allowCredentials = "true")
public class BookAppointmentController {

   @Autowired private BookAppointmentService appointmentService;
   
   

// ✅ Create new appointment
   @CrossOrigin(origins = "https://stylehub-apps.netlify.app", allowedHeaders = "*", allowCredentials = "true")
   @PostMapping("/book")
   public ResponseEntity<ApiResponse<BookAppointment>> createAppointment(
	        @RequestBody BookAppointmentRequest appointmentRequest) {
	     System.out.println(appointmentRequest);
	     
	     
	    BookAppointment saved = appointmentService.bookAppointment(appointmentRequest);

	    ApiResponse<BookAppointment> response = ApiResponse.<BookAppointment>builder()
	            .status(HttpStatus.CREATED.value())
	            .message("Appointment booked successfully ✅")
	            .timestamp(LocalDateTime.now())
//	            .data(saved)
	            .build();

	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
   
   
   // ✅ Get all appointments
   @GetMapping("/getAll")
   public ResponseEntity<ApiResponse<List<BookAppointment>>> getAllAppointments() {
	   System.out.println("call");
       List<BookAppointment> appointments = appointmentService.getAllAppointments();

       ApiResponse<List<BookAppointment>> response = ApiResponse.<List<BookAppointment>>builder()
               .status(HttpStatus.OK.value())
               .message("Appointments fetched successfully")
               .timestamp(LocalDateTime.now())
               .data(appointments)
               .build();
       return ResponseEntity.ok(response);
   }

   // ✅ Get single appointment by ID
   @GetMapping("/{id}")
   public ResponseEntity<ApiResponse<BookAppointment>> getAppointmentById(@PathVariable Long id) {
       BookAppointment appointment = appointmentService.getAppointmentById(id);

       ApiResponse<BookAppointment> response = ApiResponse.<BookAppointment>builder()
               .status(HttpStatus.OK.value())
               .message("Appointment details fetched successfully")
               .timestamp(LocalDateTime.now())
               .data(appointment)
               .build();

       return ResponseEntity.ok(response);
   }
   
   @PutMapping("/status")
	public ResponseEntity<ApiResponse<Void>> updateAppointmentStatus(
			@RequestBody UpdateAppointmentStatusRequest request) {
		System.out.println("start updateappointmentstatus");
		appointmentService.updateAppointmentStatus(request);
		ApiResponse<Void> response = ApiResponse.<Void>builder().timestamp(LocalDateTime.now())
				.status(HttpStatus.OK.value()).message("Appointment status updated to " + request.getNewStatus())
				.build();
		return ResponseEntity.ok(response);
	}

   @GetMapping("/{id}/confirm")
   public ResponseEntity<String> confirmAppointment(@PathVariable long id) {
       UpdateAppointmentStatusRequest req = new UpdateAppointmentStatusRequest(id, AppointmentStatus.CONFIRMED);
       appointmentService.updateAppointmentStatus(req);

       String html = "<html><body style='font-family: Arial; text-align:center; margin-top:50px;'>"
               + "<h2 style='color:green;'>✅ Appointment #" + id + " has been confirmed successfully!</h2>"
               + "<p>You can now close this page.</p>"
               + "</body></html>";

       return ResponseEntity.ok()
               .contentType(MediaType.TEXT_HTML)
               .body(html);
   }

   @GetMapping("/{id}/cancel")
   public ResponseEntity<String> cancelAppointment(@PathVariable long id) {
       UpdateAppointmentStatusRequest req = new UpdateAppointmentStatusRequest(id, AppointmentStatus.CANCELLED);
       appointmentService.updateAppointmentStatus(req);

       String html = "<html><body style='font-family: Arial; text-align:center; margin-top:50px;'>"
               + "<h2 style='color:red;'>❌ Appointment #" + id + " has been cancelled!</h2>"
               + "<p>You can now close this page.</p>"
               + "</body></html>";

       return ResponseEntity.ok()
               .contentType(MediaType.TEXT_HTML)
               .body(html);
   }


}