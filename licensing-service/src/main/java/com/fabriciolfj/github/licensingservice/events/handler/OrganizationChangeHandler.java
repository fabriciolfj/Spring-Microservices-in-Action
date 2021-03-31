package com.fabriciolfj.github.licensingservice.events.handler;

import com.fabriciolfj.github.licensingservice.events.enuns.ActionEnuns;
import com.fabriciolfj.github.licensingservice.events.model.OrganizationChangeModel;
import com.fabriciolfj.github.licensingservice.mapper.OrganizationMapper;
import com.fabriciolfj.github.licensingservice.service.client.CustomChannels;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrganizationChangeHandler {

    private final OrganizationMapper mapper;

    @StreamListener(CustomChannels.INPUT)
    public void updateCache(final OrganizationChangeModel model) {
        var action = ActionEnuns.toEnum(model.getAction());

        switch (action) {

            case CREATE:
                log.info("Salvando dados no redis");
                break;
            case DELETE:
                log.info("Retirando os dados do redis");
            default:
                throw new RuntimeException("Ação não mapeada.");
        }
    }
}
