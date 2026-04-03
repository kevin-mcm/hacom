package com.hacom.telco.infrastructure.adapter.smpp;

import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.type.Address;
import com.cloudhopper.smpp.type.SmppInvalidArgumentException;
import com.hacom.telco.domain.port.out.SmsPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class SmppSmsAdapter implements SmsPort {

    private final SmppSessionProvider smppSessionProvider;

    @Override
    public void sendSms(String phoneNumber, String message) {
        log.info("Sending SMS to {}", phoneNumber);
        log.debug("SMS message content: {}", message);
        try {
            SmppSession session = smppSessionProvider.getSession();
            if (session == null || !session.isBound()) {
                log.warn("SMPP session not available, skipping SMS to {}", phoneNumber);
                return;
            }
            SubmitSm submit = new SubmitSm();
            submit.setSourceAddress(new Address((byte) 0x03, (byte) 0x00, "HACOM"));
            submit.setDestAddress(new Address((byte) 0x01, (byte) 0x01, phoneNumber));
            submit.setDataCoding((byte) 0x00);
            submit.setShortMessage(CharsetUtil.encode(message, CharsetUtil.CHARSET_GSM));
            session.submit(submit, 10000);
            log.info("SMS sent successfully to {}", phoneNumber);
        } catch (SmppInvalidArgumentException e) {
            log.error("Invalid SMPP argument while sending SMS to {}: {}", phoneNumber, e.getMessage(), e);
        } catch (Exception e) {
            log.warn("Failed to send SMS to {}: {}", phoneNumber, e.getMessage());
        }
    }
}
