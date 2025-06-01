package com.ams.airManagement.service.implement;

import com.ams.airManagement.dto.PaymentDTO;
import com.ams.airManagement.dto.ResponseDTO;
import com.ams.airManagement.entity.Bookings;
import com.ams.airManagement.entity.Flights;
import com.ams.airManagement.entity.Payment;
import com.ams.airManagement.exception.OurException;
import com.ams.airManagement.repository.BookingsRepository;
import com.ams.airManagement.repository.PaymentRepository;
import com.ams.airManagement.service.interfac.NotificationServiceInterface;
import com.ams.airManagement.service.interfac.PaymentServiceInterface;
import com.ams.airManagement.utils.EmailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

@Service
public class PaymentServiceImpl implements PaymentServiceInterface {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private BookingsRepository bookingsRepository;
    @Autowired
    private NotificationServiceInterface notificationServiceInterface;

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

            try {
                String toEmail = bookings.getUser().getEmail();
                String subject = "Xác nhận thanh toán thành công cho chuyến bay của bạn - Booking ID: #" + bookings.getBookingId();

                Flights flight = bookings.getFlight();
                String passengerName = bookings.getUser().getUsername();
                Integer ticketQuantity = bookings.getTicketQuantity();

                BigDecimal totalAmountPaid = new BigDecimal(flight.getTotalPrice()).multiply(new BigDecimal(ticketQuantity));

                DecimalFormat formatter = new DecimalFormat("#,###");

                String body = "Kính gửi " + passengerName + ",\n\n" +
                        "Chúng tôi xin thông báo rằng thanh toán cho đặt chỗ chuyến bay của bạn đã được xác nhận thành công!\n" +
                        "Dưới đây là chi tiết đặt chỗ của bạn:\n\n" +
                        "----------------------------------------------------\n" +
                        "**Mã Booking:** " + bookings.getBookingId() + "\n" +
                        "**Tên hành khách:** " + passengerName + "\n" +
                        "**Số lượng vé:** " + ticketQuantity + "\n" +
                        "**Tổng số tiền đã thanh toán:** " + formatter.format(totalAmountPaid) + " VND\n" +
                        "**Trạng thái Booking:** " + bookings.getStatus() + "\n" +
                        "----------------------------------------------------\n\n" +
                        "**Chi tiết chuyến bay:**\n" +
                        "**Hãng hàng không:** " + flight.getAirline() + "\n" +
                        "**Mã chuyến bay:** " + flight.getFlightCode() + " (" + flight.getFlightId() + ")\n" +
                        "**Ngày khởi hành:** " + flight.getTakeoffDate() + "\n" +
                        "**Thời gian khởi hành:** " + flight.getTakeoffTime() + "\n" +
                        "**Điểm xuất phát:** " + flight.getDepartureProvince().getProvinceName() + "\n" +
                        "**Ngày đến:** " + flight.getLandingDate() + "\n" +
                        "**Thời gian đến:** " + flight.getLandingTime() + "\n" +
                        "**Điểm đến:** " + flight.getDestinationProvince().getProvinceName() + "\n" +
                        "**Hạng ghế:** " + flight.getSeatClass() + "\n\n" +
                        "Vui lòng lưu giữ email này để tham khảo thông tin chuyến bay. Chúng tôi khuyến nghị bạn nên đến sân bay sớm để làm thủ tục check-in.\n\n" +
                        "Nếu bạn có bất kỳ câu hỏi nào hoặc cần thay đổi đặt chỗ, xin đừng ngần ngại liên hệ với chúng tôi qua email support@airmanagement.com hoặc số điện thoại hỗ trợ 1900-XXXX.\n\n" +
                        "Cảm ơn bạn đã lựa chọn dịch vụ của chúng tôi. Chúc bạn có một chuyến đi an toàn và thoải mái!\n\n" +
                        "Trân trọng,\n" +
                        "Đội ngũ AirManagement";

                notificationServiceInterface.sendPaymentConfirmation(toEmail, subject, body);

            } catch (Exception e) {
                System.err.println("Error sending payment confirmation email for booking ID " + bookings.getBookingId() + ": " + e.getMessage());
                throw new RuntimeException("Lỗi khi gửi email xác nhận thanh toán!", e);
            }

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
