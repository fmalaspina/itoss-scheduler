package com.frsi.itoss.model.ct;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.frsi.itoss.model.collector.Collector;
import com.frsi.itoss.model.company.Company;
import com.frsi.itoss.model.company.Contact;
import com.frsi.itoss.model.location.Location;
import com.frsi.itoss.model.profile.MonitoringProfile;
import com.frsi.itoss.model.statemachine.CtState;
import com.frsi.itoss.model.user.UserAccount;
import com.frsi.itoss.model.workgroup.Workgroup;
import com.frsi.itoss.shared.Attribute;
import com.frsi.itoss.shared.CtStatus;
import com.frsi.itoss.shared.InstrumentationParameterValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.java.Log;

import java.io.Serializable;
import java.util.*;

@Log
@Data
@AllArgsConstructor
public class CtBulkAdapter implements Serializable {

    private static final long serialVersionUID = 1L;
    List<Long> tennantIds = new ArrayList<Long>();
    private String name;
    private String integrationId;
    private String environment;
    private List<InstrumentationParameterValue> instrumentationParameterValues;
    private List<Attribute> attributes = new ArrayList<Attribute>();
    private MonitoringProfile monitoringProfile;
    private Collector collector;
    private CtType type;
    private Company company;
    private Workgroup workgroup;
    private Contact contact;
    private UserAccount supportUser;
    private Location location;
    private CtState state = CtState.DELIVERY;
    private Set<CtHistory> ctHistory = new HashSet<>();
    private CtStatus status;
    @JsonIgnore
    private String oldPassword;
    @JsonIgnore
    private Map<String, Object> oldCryptedPropertyValues = new HashMap<>();

    public CtBulkAdapter() {
        super();
    }


}
