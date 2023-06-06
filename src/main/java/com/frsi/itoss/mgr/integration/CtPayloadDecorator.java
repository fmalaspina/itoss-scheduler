package com.frsi.itoss.mgr.integration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.frsi.itoss.model.company.Company;
import com.frsi.itoss.model.company.Contact;
import com.frsi.itoss.model.ct.Ct;
import com.frsi.itoss.model.location.Location;
import com.frsi.itoss.model.profile.MonitoringProfile;
import com.frsi.itoss.model.repository.CtRelationRepo;
import com.frsi.itoss.model.statemachine.CtState;
import com.frsi.itoss.model.user.UserAccount;
import com.frsi.itoss.model.workgroup.Workgroup;
import com.frsi.itoss.shared.CtStatus;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CtPayloadDecorator implements Serializable {


    @JsonIgnore
    public Ct ct;

    public CtPayloadDecorator(Ct ct) {
        this.ct = ct;

    }



    public Long getId() {
        return ct.getId();
    }


    public CtState getState() {
        return ct.getState();
    }

    public String getName() {
        return ct.getName();
    }

    public String getIntegrationId() {
        return ct.getIntegrationId();
    }

    public String getEnvironment() {
        return ct.getEnvironment();
    }

    public UserAccount getSupportUser() {
        return ct.getSupportUser();
    }


    public Location getLocation() {
        return ct.getLocation();
    }

    public MonitoringProfile getMonitoringProfile() {
        return ct.getMonitoringProfile();
    }

    public String getType() {
        return ct.getType().getName();
    }


    public Contact getContact() {
        return ct.getContact();
    }

    public Company getCompany() {
        return ct.getCompany();
    }

    public CtStatus getStatus() {
        return ct.getStatus();
    }

    public Workgroup getWorkgroup() {
        return ct.getWorkgroup();
    }

    public List<Long> getDependsOn(CtRelationRepo ctRelationRepo) {
        return ct.getDependsOn(ctRelationRepo);
    }

    public Map<String, Object> getAttributesAsMap() {
        Map<String, Object> map = new HashMap<>();
        if (!this.ct.getJoinedAtts().isEmpty()) {
            return this.ct.getJoinedAtts().stream().collect(Collectors.toMap(kv -> kv.getName(), kv -> kv.getValue() == null ? "" : kv.getValue()));
        } else {
            return map;
        }
    }
}
