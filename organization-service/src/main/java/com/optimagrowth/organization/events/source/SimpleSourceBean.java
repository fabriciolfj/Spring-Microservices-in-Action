package com.optimagrowth.organization.events.source;

import com.optimagrowth.organization.clients.CustomChannels;
import com.optimagrowth.organization.events.enuns.ActionEnuns;
import com.optimagrowth.organization.events.model.OrganizationChangeModel;
import com.optimagrowth.organization.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleSourceBean {

    private final CustomChannels source;

    public void publishOrganizationChange(final ActionEnuns action, final String organizationId) {
        log.info("Sending kafka message {} for Organization id: {}", action.getDescription(), organizationId);
        var change = new OrganizationChangeModel(OrganizationChangeModel.class.getTypeName(), action.getDescription(), organizationId, UserContext.getCorrelationId());
        source.orgs().send(MessageBuilder.withPayload(change).build());
    }
}
