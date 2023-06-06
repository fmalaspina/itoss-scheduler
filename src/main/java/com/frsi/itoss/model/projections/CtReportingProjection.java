package com.frsi.itoss.model.projections;

import com.frsi.itoss.model.company.ContactStatus;
import com.frsi.itoss.model.user.UserStatus;

import java.util.Date;

/**
 * A Projection for the {@link com.frsi.itoss.model.ct.Ct} entity
 */
public interface CtReportingProjection {
    Long getId();

    String getName();

    String getIntegrationId();

    String getEnvironment();

    String getState();

    MonitoringProfileInfo getMonitoringProfile();

    CtTypeInfo getType();

    CompanyInfo getCompany();

    ContactInfo getContact();

    UserAccountInfo getSupportUser();

    LocationInfo getLocation();

    CtStatusInfo getStatus();

    /**
     * A Projection for the {@link com.frsi.itoss.model.profile.MonitoringProfile} entity
     */
    interface MonitoringProfileInfo {
        Long getId();

        String getName();

        CtTypeInfo getCtType();

        /**
         * A Projection for the {@link com.frsi.itoss.model.ct.CtType} entity
         */
        interface CtTypeInfo {
            Long getId();

            String getName();

        }
    }

    /**
     * A Projection for the {@link com.frsi.itoss.model.ct.CtType} entity
     */
    interface CtTypeInfo {
        Long getId();

        String getName();

    }

    /**
     * A Projection for the {@link com.frsi.itoss.model.company.Company} entity
     */
    interface CompanyInfo {
        Long getId();

        String getName();

        String getIntegrationId();

        LocationInfo getLocation();

        CompanyTypeInfo getType();

        /**
         * A Projection for the {@link com.frsi.itoss.model.location.Location} entity
         */
        interface LocationInfo {
            Long getId();

            String getName();

            LocationInfo getParent();

            /**
             * A Projection for the {@link com.frsi.itoss.model.location.LocationType} entity
             */
            interface LocationTypeInfo {
                Long getId();

                String getName();
            }
        }

        /**
         * A Projection for the {@link com.frsi.itoss.model.company.CompanyType} entity
         */
        interface CompanyTypeInfo {
            Long getId();

            String getName();
        }
    }

    /**
     * A Projection for the {@link com.frsi.itoss.model.company.Contact} entity
     */
    interface ContactInfo {
        Long getId();

        String getName();

        String getEmail();

        ContactStatus getStatus();


        CompanyInfo getCompany();


        /**
         * A Projection for the {@link com.frsi.itoss.model.company.Company} entity
         */
        interface CompanyInfo {
            String getName();

            String getIntegrationId();

            LocationInfo getLocation();

            CompanyTypeInfo getType();

            /**
             * A Projection for the {@link com.frsi.itoss.model.location.Location} entity
             */
            interface LocationInfo {
                String getName();

                LocationInfo getParent();


            }

            /**
             * A Projection for the {@link com.frsi.itoss.model.company.CompanyType} entity
             */
            interface CompanyTypeInfo {
                Long getId();

                String getName();
            }
        }
    }

    /**
     * A Projection for the {@link com.frsi.itoss.model.user.UserAccount} entity
     */
    interface UserAccountInfo {
        Long getId();

        String getName();


        String getEmail();

        String getUsername();

        UserStatus getStatus();





    }

    /**
     * A Projection for the {@link com.frsi.itoss.model.location.Location} entity
     */
    interface LocationInfo {
        Long getId();

        String getName();

        LocationInfo getParent();

    }

    /**
     * A Projection for the {@link com.frsi.itoss.shared.CtStatus} entity
     */
    interface CtStatusInfo {
        Long getId();

        boolean isDown();

        Date getLastStatusChange();
    }
}