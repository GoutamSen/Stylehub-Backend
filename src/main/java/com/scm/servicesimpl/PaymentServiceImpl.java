package com.scm.servicesimpl;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.scm.services.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

	private static final String KEY = "rzp_test_xxxxx";   // from Razorpay dashboard
    private static final String SECRET = "your_secret_key";
    
    
	
	@Override
	public Order createRazorpayOrder(Double amount) throws RazorpayException {
		 RazorpayClient client = new RazorpayClient(KEY, SECRET);

	        JSONObject options = new JSONObject();
	        options.put("amount", (int)(amount * 100)); // Razorpay expects paise
	        options.put("currency", "INR");
	        options.put("payment_capture", 1);

	        return client.orders.create(options);
	}

}
