package com.atparui.rmsservice.domain;

import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.data.r2dbc.mapping.event.AfterConvertCallback;
import org.springframework.data.r2dbc.mapping.event.AfterSaveCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TableWaiterAssignmentCallback
    implements AfterSaveCallback<TableWaiterAssignment>, AfterConvertCallback<TableWaiterAssignment> {

    @Override
    public Publisher<TableWaiterAssignment> onAfterConvert(TableWaiterAssignment entity, SqlIdentifier table) {
        return Mono.just(entity.setIsPersisted());
    }

    @Override
    public Publisher<TableWaiterAssignment> onAfterSave(TableWaiterAssignment entity, OutboundRow outboundRow, SqlIdentifier table) {
        return Mono.just(entity.setIsPersisted());
    }
}
