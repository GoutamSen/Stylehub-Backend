package com.scm.requests;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookAppointmentRequest {

	    private LocalDate date;
	    private LocalTime time;
	    private String notes;
	    private String email;
	    private List<Long> serviceImageIds;  // IDs of selected services
	    
}
