package com.optimagrowth.organization.clients;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface CustomChannels {

    @Output("outboundOrgChanges")
    MessageChannel orgs();
}
