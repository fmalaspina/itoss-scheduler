package com.frsi.itoss.model.baseclasses;

import com.frsi.itoss.model.SpringContext;
import com.frsi.itoss.model.ct.Ct;
import com.frsi.itoss.model.profile.EventRule;
import com.frsi.itoss.model.profile.Metric;
import com.frsi.itoss.model.profile.Monitor;
import com.frsi.itoss.model.repository.MetricRepo;
import com.frsi.itoss.model.repository.MonitoringProfileRepo;
import com.frsi.itoss.model.tennant.Tennant;
import com.frsi.itoss.shared.MetricCategory;
import org.mvel2.MVEL;
import org.quartz.CronScheduleBuilder;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;


public class ValidationListener {


    @PreRemove
    void beforeAnyRemove(Object object) {
        if (object instanceof Tennant) {
            if (!((Tennant) object).getCts().isEmpty() || !((Tennant) object).getUsers().isEmpty()) {

                throw new CustomValidationException(
                        "There are related users or cts. Delete the relationship first.");
            }
        }

    }


    @PreUpdate
    @PrePersist
    void beforeAnyUpdate(Object object) {


        if (object instanceof Ct) {

            // TODO Se comenta porque cuando se hace PATCH de actualización el monitoringProfile viene nulo y solamente con el id. Cuando se hizo este cambio para buscar el profile el sistema se puso lento.
            //MonitoringProfileRepo mpRepo = SpringContext.getBean(MonitoringProfileRepo.class);
            //MonitoringProfile mp = mpRepo.findById(((Ct)object).getMonitoringProfile().getId()).get();

//			if (!mp.getCtType().getId().equals(((Ct)object).getType().getId())) {
//				throw new CustomValidationException(
//						Arrays.asList("Monitoring profile assigned is not for this Ct type."));
//			}
            if (!((Ct) object).validateAttributes()) {
                throw new CustomValidationException(
                        "Monitoring profile assigned need undefined attribute in Ct.");
            }
            if (!((Ct) object).validateTimeZone()) {
                throw new CustomValidationException(
                        "Bad timezone attribute format.");
            }
            if (!((Ct) object).validateIp()) {
                throw new CustomValidationException(
                        "Bad IP attribute format.");
            }
            if (!((Ct) object).validateEnvironment()) {
                throw new CustomValidationException(
                        "Bad environment.");
            }


        }

        if (object instanceof Monitor) {
            String fe = ((Monitor) object).getFrequencyExpression();
            try {
                CronScheduleBuilder.cronScheduleNonvalidatedExpression(fe);
            } catch (Exception e) {
                throw new CustomValidationException(
                        "Error parsing cron expression " + e.getMessage());
            }
        }


        if (object instanceof EventRule) {
            if (((EventRule) object).getCondition() == null || ((EventRule) object).getCondition().isBlank()) {
                throw new CustomValidationException("Condition must be defined.");
            } else {
                if (((EventRule) object).isActive()) {
                    try {
                        MVEL.compileExpression(((EventRule) object).getCondition());
                    } catch (Exception e) {
                        throw new CustomValidationException("Condition grammar has errors. " + e.getMessage());
                    }

                }
            }
        }
        if (object instanceof EventRule) {
            if (((EventRule) object).getActions() == null || ((EventRule) object).getActions().isBlank()) {
                throw new CustomValidationException("Action must be defined.");
            }
            if (((EventRule) object).isActive()) {
                try {
                    MVEL.compileExpression(((EventRule) object).getActions());
                } catch (Exception e) {
                    throw new CustomValidationException("Action grammar has errors. " + e.getMessage());
                }

            }
        }

        if (object instanceof EventRule) {
            if (((EventRule) object).getPhase() == null) {
                throw new CustomValidationException("Phase must be defined.");
            }
        }

    }


}
