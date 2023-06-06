package com.frsi.itoss.model.continuousqueries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Component

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricDefaults implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private List<Granularity> granularities = Arrays.asList(

            // EXTRA SMALL - 2 meses
            new Granularity("1h", "1 hour", "2 hours", "EXTRA_SMALL", "30 minutes", "Keep 1 hour data samples for 2 months."),
            // SMALL - 3 meses
            new Granularity("4h", "4 hour", "8 hours", "SMALL", "1 hour", "Keep 4 hours data samples for 3 months."),
            new Granularity("6h", "6 hours", "12 hours", "SMALL", "2 hours", "Keep 6 hours data samples for 2 months."),
            // MEDIUM - 6 meses
            new Granularity("12h", "12 hours", "24 hours", "MEDIUM", "4 hours", "Keep 12 hours data samples for 6 months."),
            // LARGE - 1 year
            new Granularity("1d", "24 hours", "48 hours", "LARGE", "6 hours", "Keep 24 hours data samples for 1 year."),
            // EXTRA LARGE - 2 años
            new Granularity("1w", "1 week", "2 weeks", "EXTRA_LARGE", "24 hours", "Keep 1 week data samples for 2 years."),
            new Granularity("1m", "4 weeks", "8 weeks", "EXTRA_LARGE", "24 hours", "Keep 1 month data samples for 2 years.")

    );
    private List<Function> functions = Arrays.asList(

            new Function("FIRST", "First field value with the oldest timestamp", "Save first value with the oldest timestamp for all fields."),
            new Function("LAST", "Last field value with the most recent timestamp", "Save last field value with the most recent timestamp for all fields."),
            new Function("MIN", "Lowest field value", "Save lowest field value for all fields."),
            new Function("MAX", "Greatest field value", "Save greatest value for all fields."),
            new Function("MEAN", "Average of field values", "Save average of all field values")//,
            //  new Function("MEDIAN", "Middle value from a sorted list of field values", "Save middle value from a sorted list of all field values")
    );


    @Data
    @AllArgsConstructor
    public static class Granularity implements Serializable {

        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private String id;
        private String name;
        private String offset;
        private String bucket;
        private String refreshInterval;
        private String description;
    }

    @Data
    @AllArgsConstructor
    public static class Function implements Serializable {

        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private String id;
        private String name;
        private String description;

    }

}
