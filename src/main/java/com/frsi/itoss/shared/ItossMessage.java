package com.frsi.itoss.shared;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItossMessage implements Serializable {
    private UUID id;
    private String timestamp;
    private MonitorCtStatus status;
    private ArrayList<ManagerAction> actions;
}
