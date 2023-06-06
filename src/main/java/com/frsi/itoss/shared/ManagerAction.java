package com.frsi.itoss.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagerAction<T> implements Serializable {

    private ItossOperation operation;
    private T actionObject;


}

