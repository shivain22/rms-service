package com.atparui.rmsservice.domain;

import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.data.r2dbc.mapping.event.AfterConvertCallback;
import org.springframework.data.r2dbc.mapping.event.AfterSaveCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class OrderItemCustomizationCallback
    implements AfterSaveCallback<OrderItemCustomization>, AfterConvertCallback<OrderItemCustomization> {

    @Override
    public Publisher<OrderItemCustomization> onAfterConvert(OrderItemCustomization entity, SqlIdentifier table) {
        return Mono.just(entity.setIsPersisted());
    }

    @Override
    public Publisher<OrderItemCustomization> onAfterSave(OrderItemCustomization entity, OutboundRow outboundRow, SqlIdentifier table) {
        return Mono.just(entity.setIsPersisted());
    }
}
