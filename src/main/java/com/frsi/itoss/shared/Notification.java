package com.frsi.itoss.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private String type;
    private Long ctId;
    private Date timestamp = new Date();
    private HashMap<String, String> payload = new HashMap<>();
    private List<String> tags = new ArrayList<>();
    private String[] destinations;
    private Set<Long> userIds = new HashSet<>();

    @Override
    public String toString() {
        return "title=" + title + ", type=" + type + ", payload=" + payload + ", tags=" + tags
                + "\n\n";
    }


}
