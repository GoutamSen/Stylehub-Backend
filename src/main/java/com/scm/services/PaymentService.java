package com.scm.services;

import com.razorpay.Order;
import com.razorpay.RazorpayException;

public interface PaymentService {

	public Order createRazorpayOrder(Double amount) throws RazorpayException;
}
