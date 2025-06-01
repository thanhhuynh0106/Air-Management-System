package com.ams.airManagement.service.interfac;

public interface NotificationServiceInterface {
    void sendPaymentConfirmation(String to, String subject, String body);
}