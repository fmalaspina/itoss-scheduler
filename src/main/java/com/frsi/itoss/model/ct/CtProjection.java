package com.frsi.itoss.model.ct;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.frsi.itoss.model.collector.Collector;
import com.frsi.itoss.model.company.Company;
import com.frsi.itoss.model.company.Contact;
import com.frsi.itoss.model.location.Location;
import com.frsi.itoss.model.profile.MonitoringProfile;
import com.frsi.itoss.model.user.UserAccount;
import com.frsi.itoss.model.workgroup.Workgroup;
import com.frsi.itoss.shared.Attribute;
import com.frsi.itoss.shared.InstrumentationParameterValue;

import java.util.List;

public interface CtProjection {
    public Long getId();
    public String getName();
    public UserAccount getSupportUser();
    public Collector getCollector();
    public CtType getType();
    public MonitoringProfile getMonitoringProfile();
    public Location getLocation();
    public Contact getContact();
    public Workgroup getWorkgroup();
    public Company getCompany();
    public String getEnvironment();
    @JsonDeserialize(as = List.class, contentAs = Attribute.class)
    public Object getAttributes();
    @JsonDeserialize(as = List.class, contentAs = InstrumentationParameterValue.class)
    public Object getInstrumentationParameterValues();



}