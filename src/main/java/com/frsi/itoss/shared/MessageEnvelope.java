package com.frsi.itoss.shared;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class MessageEnvelope implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    UUID componentId;
    String event;

}