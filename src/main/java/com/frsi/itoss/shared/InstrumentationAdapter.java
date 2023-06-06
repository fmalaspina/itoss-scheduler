package com.frsi.itoss.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstrumentationAdapter implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public String name;
    public String description;
    private List<InstrumentationParameter> instrumentationParameters = new ArrayList<InstrumentationParameter>();


    public String getName() {
        return this.name;
    }

    public DataType getDataType(String parameterName) {
        Optional<InstrumentationParameter> optionalInstrumParam = this.instrumentationParameters.stream().filter(p -> p.getName().equalsIgnoreCase(parameterName)).findFirst();
        if (optionalInstrumParam.isPresent()) return optionalInstrumParam.get().getType();
        return DataType.TEXT;
    }

}
