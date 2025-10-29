package com.scm.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private LocalTime time;
    private String notes;
    private Double totalAmount;
    private Double amountPaid;
    private Double remainingAmount;
    private PaymentStatus paymentStatus;     // UNPAID, PARTIALLY_PAID, FULLY_PAID
    private LocalDateTime confirmationTime; // store when admin confirmed
    private String razorpayOrderId;
    
    
    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_status", length = 20)
    private AppointmentStatus appointmentStatus;
    
    
    @ManyToMany(fetch =  FetchType.EAGER)
    @JoinTable(
        name = "appointment_service_image",
        joinColumns = @JoinColumn(name = "appointment_id"),
        inverseJoinColumns = @JoinColumn(name = "service_image_id")
    )
    private List<ServiceImage> serviceImages = new ArrayList<>();
    
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id")
    @JsonIgnoreProperties({"appointments"}) // avoid recursion
    private User user;
}

