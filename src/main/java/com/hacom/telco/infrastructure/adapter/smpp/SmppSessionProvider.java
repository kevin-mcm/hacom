package com.hacom.telco.infrastructure.adapter.smpp;

import com.cloudhopper.smpp.SmppClient;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Log4j2
@Component
public class SmppSessionProvider {

    @Value("${smpp.host:localhost}")
    private String smppHost;

    @Value("${smpp.port:2775}")
    private int smppPort;

    @Value("${smpp.systemId:smppclient}")
    private String systemId;

    @Value("${smpp.password:password}")
    private String password;

    private SmppClient smppClient;
    private SmppSession smppSession;

    @PostConstruct
    public void init() {
        smppClient = new DefaultSmppClient();
        tryConnect();
    }

    private void tryConnect() {
        try {
            SmppSessionConfiguration config = new SmppSessionConfiguration();
            config.setHost(smppHost);
            config.setPort(smppPort);
            config.setSystemId(systemId);
            config.setPassword(password);
            config.setConnectTimeout(10000);
            config.setRequestExpiryTimeout(30000);
            smppSession = smppClient.bind(config, null);
            log.info("SMPP session established to {}:{}", smppHost, smppPort);
        } catch (SmppChannelException | SmppTimeoutException | UnrecoverablePduException e) {
            log.warn("Could not establish SMPP session (will continue without SMS): {}", e.getMessage());
            smppSession = null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Interrupted while establishing SMPP session: {}", e.getMessage());
            smppSession = null;
        }
    }

    public SmppSession getSession() {
        if (smppSession == null || !smppSession.isBound()) {
            log.warn("SMPP session not available, attempting reconnect...");
            tryConnect();
        }
        return smppSession;
    }

    @PreDestroy
    public void destroy() {
        if (smppSession != null && smppSession.isBound()) {
            smppSession.unbind(5000);
        }
        if (smppClient != null) {
            smppClient.destroy();
        }
    }
}
