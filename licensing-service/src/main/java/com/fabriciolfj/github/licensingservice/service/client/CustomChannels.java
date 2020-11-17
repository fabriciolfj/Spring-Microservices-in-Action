package com.fabriciolfj.github.licensingservice.service.client;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CustomChannels {

    String INPUT = "inboundOrgChanges";

    @Input(INPUT)
    SubscribableChannel orgs();
}
