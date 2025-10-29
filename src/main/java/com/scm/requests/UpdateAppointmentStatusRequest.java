package com.scm.requests;

import com.scm.entities.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateAppointmentStatusRequest {

	private long id;
	private AppointmentStatus newStatus;
	
}
