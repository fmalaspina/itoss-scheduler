package com.frsi.itoss.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectorConfiguration implements Serializable {
    /**
     *
     */


    private static final long serialVersionUID = 1L;

    private Set<MonitorConfiguration> monitors = new HashSet<MonitorConfiguration>();

    public Set<Long> getCtIds() {
        Set<Long> ctIds = this.monitors.stream()
                .flatMap(m -> m.getCtConfiguration().stream()).map(ctConf -> Long.valueOf(ctConf.getCtId())).collect(Collectors.toSet());
        return ctIds;

    }
//	
//	public List<String> getCtsToBeCollectedByMetric(String metricName) { 
//		return this.monitors.stream()
//			.filter((mConf) -> mConf.getMetricName().equals(metricName)).findFirst().get().getCtConfiguration().stream().map(h ->  h.getCtId().toString()).collect(Collectors.toList());
//	}
//	public List<String> getCtsToBeCollectedByInstrum(String instrumName) { 
//		return this.monitors.stream()
//			.filter((mConf) -> mConf.getInstrumentationAdapter().getName().equals(instrumName)).findFirst().get().getCtConfiguration().stream().map(h -> h.getCtId().toString()).collect(Collectors.toList());
//	}

    public Optional<MonitorConfiguration> getMonitor(Long id) {
        return this.monitors.stream().filter(m -> m.getMonitorId() == id).findFirst();


    }
}
