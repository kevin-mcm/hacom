package com.hacom.telco.domain.port.out;

public interface SmsPort {
    void sendSms(String phoneNumber, String message);
}
