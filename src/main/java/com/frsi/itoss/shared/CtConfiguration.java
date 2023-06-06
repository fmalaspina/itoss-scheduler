package com.frsi.itoss.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CtConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;
    private String ctId;
    private String ctName;
    private String ctType;
    private String environment = "<none>";
    private String workgroup = "<none>";
    private String supportUser = "<none>";
    private String company = "<none>";
    private Long companyId;
    //private List<Attribute> ctAttributes = new ArrayList<>();
    private List<Attribute> ctAttributes = new ArrayList<>();
    private List<Attribute> companyAttributes = new ArrayList<>();
    private List<TypeAttribute> typeAttributes = new ArrayList<>();
    private String profileName;
    private String profileId;
    private List<InstrumentationParameterValue> ctInstrumParamValues = new ArrayList<InstrumentationParameterValue>();


    public Map<String, Object> getParamValues() {
        Map<String, Object> params = new HashMap<String, Object>();
        this.ctInstrumParamValues.stream().forEach(a -> params.put(a.getName(), a.getValue()));
        this.ctAttributes.stream().forEach(a -> params.put(a.getName(), a.getValue()));
        return params;

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CtConfiguration other = (CtConfiguration) obj;
        if (ctId == null) {
            if (other.ctId != null)
                return false;
        } else if (!ctId.equals(other.ctId))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ctId == null) ? 0 : ctId.hashCode());
        return result;
    }

    public String getStringValue(String propertyName) {

        return (String) getObject(propertyName);
    }

    public Object getAttribute(String propertyName) {
        Object result = null;
        try {
            result = getObject(propertyName);

        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    public String getNameValue(String propertyName) {

        return ((LinkedHashMap) getObject(propertyName)).get("value").toString();
    }

    public int getIntValue(String propertyName) {

        return (int) getObject(propertyName);
    }

    //	public Object getObject(String propertyName) {
//	
//			return this.ctAttributes.stream().filter(o -> o.getName().trim().equalsIgnoreCase(propertyName.trim()))
//					.findFirst().get().getValue();
//	
//
//	}
    public Object getObject(String propertyName) {

        var obj = getParamValues().entrySet().stream().filter(o -> o.getKey().trim().equalsIgnoreCase(propertyName.trim()))
                .findFirst().get().getValue();

        return obj;
    }


    public void setCtAttributes(List<Attribute> ctAttributes) {
        this.ctAttributes = ctAttributes;
    }

    //    public void add(Attribute object) {
//
//        this.ctAttributes.add(object);
//    }

}