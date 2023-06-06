package com.frsi.itoss.mgr.services;

import com.frsi.itoss.model.SpringContext;
import com.frsi.itoss.model.parameters.Parameter;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;



public class ParameterListener  {

    public static SpringContext springContext;


    @PreUpdate
    @PrePersist
    public void preUpdate(Object o) {
        if (o instanceof Parameter) {
            var managerApi = springContext.getBean(ManagerAPIImpl.class);
            managerApi.reloadScoring();
        }
    }
}
