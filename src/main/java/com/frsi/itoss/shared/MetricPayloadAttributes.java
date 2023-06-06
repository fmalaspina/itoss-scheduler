package com.frsi.itoss.shared;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricPayloadAttributes implements Serializable {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 1L;

    private List<Tag> tags = new ArrayList<>();
    private List<Field> fields = new ArrayList<>();

    @JsonIgnore
    public boolean isBootTimeDefined() {
        return fields.stream().anyMatch(f -> f.isBootTime() /* t.getFormat() != null*/);
    }

    @JsonIgnore
    public Optional<Field> getBootTimeField() {
        return fields.stream().filter(f -> f.getInternalField() == InternalField.BOOTTIME).findFirst();
    }
}
