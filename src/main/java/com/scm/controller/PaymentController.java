package com.scm.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.razorpay.Order;
import com.scm.response.ApiResponse;
import com.scm.services.BookAppointmentService;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private BookAppointmentService appointmentService;

    @PostMapping("/{id}/create-order")
    public ResponseEntity<ApiResponse<Object>> createOrder(
            @PathVariable Long id,
            @RequestBody Map<String, Object> req) {
        try {
            double amount = Double.parseDouble(req.get("amount").toString());

            // Call service to create order (e.g. Razorpay)
            Order order = appointmentService.initiatePayment(id, amount);

            // Build response with order details
            Map<String, Object> orderDetails = new HashMap<>();
            orderDetails.put("id", order.get("id"));          // Razorpay order id
            orderDetails.put("amount", order.get("amount"));
            orderDetails.put("currency", order.get("currency"));
            orderDetails.put("status", order.get("status"));

            ApiResponse<Object> response = ApiResponse.<Object>builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.OK.value())
                    .message("Order Created Successfully!")
                    .data(orderDetails) // ✅ include order details in response
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<Object> response = ApiResponse.<Object>builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.EXPECTATION_FAILED.value())
                    .message("Failed To Create Order! " + e.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
        }
    }


    @PostMapping("/{id}/verify")
    public ResponseEntity<ApiResponse<Object>> verify(
            @PathVariable Long id,
            @RequestBody Map<String, String> req) {
        try {
            String paymentId = req.get("paymentId");
            String orderId = req.get("orderId");
            String signature = req.get("signature");

            // Call service to verify Razorpay signature
            boolean isVerified = appointmentService.verifyPayment(id, paymentId, orderId, signature);

            // Build response data
            Map<String, Object> verificationResult = new HashMap<>();
            verificationResult.put("appointmentId", id);
            verificationResult.put("orderId", orderId);
            verificationResult.put("paymentId", paymentId);
            verificationResult.put("verified", isVerified);

            ApiResponse<Object> response = ApiResponse.<Object>builder()
                    .timestamp(LocalDateTime.now())
                    .status(isVerified ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value())
                    .message(isVerified ? "✅ Payment verified successfully" : "❌ Payment verification failed")
                    .data(verificationResult)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<Object> response = ApiResponse.<Object>builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Payment verification failed: " + e.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
