package com.frsi.itoss.model.statemachine;

public enum CtEvent {
    DELIVER,// enter DELIVERY state
    OPERATE, // enter OPERATIONS state
    OUTAGE_START, // enter MAINTENANCE state
    //OUTAGE_SCHEDULED, // enter OUTAGE_SCHEDULED state
    OUTAGE_END,  // enter in OPERATIONS state from MAINTENANCE
    DISABLE,  // enter OUTOFSERVICE state
    ENABLE, // enter OPERATIONS state from OUTOFSERVICE
    DISPOSE,  // enter into ENDOFSERVICE state
    ATTEND, // se genero una entrada en el histórico del ct
    CREATE_TICKET, //crea un ticket de integración y un attend si ejecuta bien
    HISTORY,

    RESET
}
