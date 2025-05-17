package com.ams.airManagement.service.implement;

import com.ams.airManagement.dto.PaymentDTO;
import com.ams.airManagement.dto.ResponseDTO;
import com.ams.airManagement.entity.Bookings;
import com.ams.airManagement.entity.Payment;
import com.ams.airManagement.exception.OurException;
import com.ams.airManagement.repository.BookingsRepository;
import com.ams.airManagement.repository.PaymentRepository;
import com.ams.airManagement.service.interfac.PaymentServiceInterface;
import com.ams.airManagement.utils.EmailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PaymentServiceImpl implements PaymentServiceInterface {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private BookingsRepository bookingsRepository;
    @Autowired
    private EmailUtils emailUtils;

    @Override
    public ResponseDTO makePayment(PaymentDTO paymentDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            Bookings bookings = bookingsRepository.findById(paymentDTO.getBookingId()).orElseThrow(() -> new OurException("Booking not found!"));

            Payment payment = Payment.builder()
                    .amount(paymentDTO.getAmount())
                    .method(paymentDTO.getMethod())
                    .paymentDate(String.valueOf(new Date()))
                    .bookings(bookings)
                    .build();

            paymentRepository.save(payment);
            bookings.setStatus("PAID");
            bookingsRepository.save(bookings);

            String to = bookings.getUser().getEmail();
            String subject = "XÁC NHẬN THANH TOÁN THÀNH CÔNG!";
            String body = "Bạn đã thanh toán thành công số tiền " + payment.getAmount() + " cho mã booking "
                    + bookings.getBookingId();

            emailUtils.sendEmail(to, subject, body);
            responseDTO.setStatusCode(200);
            responseDTO.setMessage("Successful!");
        } catch (OurException e) {
            responseDTO.setStatusCode(400);
            responseDTO.setMessage(e.getMessage());
        }
        catch (Exception e) {
            responseDTO.setStatusCode(500);
            responseDTO.setMessage("Error paying: " + e.getMessage());
        }

        return responseDTO;
    }
}
