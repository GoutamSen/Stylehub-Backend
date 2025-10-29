package com.scm.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="user_tab")
@ToString(exclude = {"appointments", "serviceImages"})
public class User {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    private String username;
	    private String password;
	    private String email;
        private String phoneNumber;
        private String address;
        
        @Lob  // Large Object
        private String image;
        
        @Enumerated(EnumType.STRING)
        @Column(length = 10)  
        private Status status;
	    @Enumerated(EnumType.STRING)
	    private Role role; // USER or ADMIN
	    
	    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL)
	    @JsonManagedReference   
	    private List<BookAppointment> appointments = new ArrayList<>();
	    
}
