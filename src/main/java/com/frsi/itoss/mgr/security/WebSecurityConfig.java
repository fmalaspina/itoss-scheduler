package com.frsi.itoss.mgr.security;

import com.frsi.itoss.CustomCorsFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.SessionManagementFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig /*extends WebSecurityConfigurerAdapter */{

    @Value("${security.enabled}")
    private boolean securityEnabled;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserDetailsService jwtUserDetailsService;


    private final PasswordEncoder bcryptEncoder;

    private final JwtRequestFilter jwtRequestFilter;

    public WebSecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, UserDetailsService jwtUserDetailsService, PasswordEncoder bcryptEncoder, JwtRequestFilter jwtRequestFilter) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.bcryptEncoder = bcryptEncoder;
        this.jwtRequestFilter = jwtRequestFilter;
    }


    @Bean
    CustomCorsFilter customCorsFilter() {
        return new CustomCorsFilter();
    }



    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(jwtUserDetailsService);
        authProvider.setPasswordEncoder(bcryptEncoder);
        return authProvider;

    }


    @Bean

    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        if (securityEnabled) {

            httpSecurity
                    .addFilterBefore(customCorsFilter(), SessionManagementFilter.class)
                    .httpBasic().disable().csrf().disable().cors().disable().authorizeHttpRequests()
                    .antMatchers("/managerApi").permitAll()

                    .antMatchers(HttpMethod.GET, "/managerApi/getConfiguration**").permitAll()
                    .antMatchers(HttpMethod.GET, "/managerApi/getCts**").permitAll()
                    .antMatchers(HttpMethod.GET, "/managerApi/getMonitors**").permitAll()
                    .antMatchers(HttpMethod.POST, "/managerApi/sendMessage**").permitAll()
                    .antMatchers(HttpMethod.GET, "/managerApi/sendMessage**").permitAll()

                    .antMatchers("/encrypt*", "/encrypt/**").permitAll()
                    //.antMatchers("/itoss-collector*", "/itoss-collector/**").permitAll()
                    //.antMatchers("/itoss-manager*", "/itoss-manager/**").permitAll()
                    .antMatchers("/stats*", "/stats/**").permitAll()
                    .antMatchers("/parameters*", "/parameters/**").permitAll()
                    .antMatchers("/news*", "/news/**").permitAll()
                    .antMatchers("/helpContents*", "/helpContents/**").permitAll()
                    .antMatchers("/zoneIds*", "/zoneIds/**").permitAll()
                    .antMatchers("/actuator*", "/actuator/**").permitAll()


                    //.antMatchers(HttpMethod.GET,"/managerApi", "/managerApi*", "/managerApi/**").hasRole("ITOSS-COLLECTOR")
                    .antMatchers(HttpMethod.GET, "/ctTypes*", "/ctTypes/**").hasAnyAuthority("CREATE_TYPES", "READ_TYPES")
                    .antMatchers(HttpMethod.POST, "/ctTypes*", "/ctTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.PATCH, "/ctTypes*", "/ctTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.OPTIONS).permitAll()
                    .antMatchers("/authenticate", "/authenticate/ldaps").permitAll()
                    .antMatchers(HttpMethod.PUT, "/ctTypes*", "/ctTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.DELETE, "/ctTypes*", "/ctTypes/**").hasAnyAuthority("CREATE_TYPES")

                    .antMatchers(HttpMethod.GET, "/contactTypes*", "/contactTypes/**").hasAnyAuthority("CREATE_TYPES", "READ_TYPES")
                    .antMatchers(HttpMethod.POST, "/contactTypes*", "/contactTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.PATCH, "/contactTypes*", "/contactTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.PUT, "/contactTypes*", "/contactTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.DELETE, "/contactTypes*", "/contactTypes/**").hasAnyAuthority("CREATE_TYPES")

                    .antMatchers(HttpMethod.GET, "/dynamicSearchTypes*", "/dynamicSearchTypes/**").hasAnyAuthority("CREATE_TYPES", "READ_TYPES")
                    .antMatchers(HttpMethod.POST, "/dynamicSearchTypes*", "/dynamicSearchTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.PATCH, "/dynamicSearchTypes*", "/dynamicSearchTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.PUT, "/dynamicSearchTypes*", "/dynamicSearchTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.DELETE, "/dynamicSearchTypes*", "/dynamicSearchTypes/**").hasAnyAuthority("CREATE_TYPES")

                    .antMatchers(HttpMethod.GET, "/workgroupTypes*", "/workgroupTypes/**").hasAnyAuthority("CREATE_TYPES", "READ_TYPES")
                    .antMatchers(HttpMethod.POST, "/workgroupTypes*", "/workgroupTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.PATCH, "/workgroupTypes*", "/workgroupTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.PUT, "/workgroupTypes*", "/workgroupTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.DELETE, "/workgroupTypes*", "/workgroupTypes/**").hasAnyAuthority("CREATE_TYPES")

                    .antMatchers(HttpMethod.GET, "/locationTypes*", "/locationTypes/**").hasAnyAuthority("CREATE_TYPES", "READ_TYPES")
                    .antMatchers(HttpMethod.POST, "/locationTypes*", "/locationTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.PATCH, "/locationTypes*", "/locationTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.PUT, "/locationTypes*", "/locationTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.DELETE, "/locationTypes*", "/locationTypes/**").hasAnyAuthority("CREATE_TYPES")

                    .antMatchers(HttpMethod.GET, "/companyTypes*", "/companyTypes/**").hasAnyAuthority("CREATE_TYPES", "READ_TYPES")
                    .antMatchers(HttpMethod.POST, "/companyTypes*", "/companyTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.PATCH, "/companyTypes*", "/companyTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.PUT, "/companyTypes*", "/companyTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.DELETE, "/companyTypes*", "/companyTypes/**").hasAnyAuthority("CREATE_TYPES")

                    .antMatchers(HttpMethod.GET, "/userAccountTypes*", "/userAccountTypes/**").hasAnyAuthority("CREATE_TYPES", "READ_TYPES")
                    .antMatchers(HttpMethod.POST, "/userAccountTypes*", "/userAccountTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.PATCH, "/userAccountTypes*", "/userAccountTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.PUT, "/userAccountTypes*", "/userAccountTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.DELETE, "/userAccountTypes*", "/userAccountTypes/**").hasAnyAuthority("CREATE_TYPES")

                    .antMatchers(HttpMethod.GET, "/dashboardTypes*", "/dashboardTypes/**").hasAnyAuthority("CREATE_TYPES", "READ_TYPES")
                    .antMatchers(HttpMethod.POST, "/dashboardTypes*", "/dashboardTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.PATCH, "/dashboardTypes*", "/dashboardTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.PUT, "/dashboardTypes*", "/dashboardTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.DELETE, "/dashboardTypes*", "/dashboardTypes/**").hasAnyAuthority("CREATE_TYPES")

                    .antMatchers(HttpMethod.GET, "/containerTypes*", "/containerTypes/**").hasAnyAuthority("CREATE_TYPES", "READ_TYPES")
                    .antMatchers(HttpMethod.POST, "/containerTypes*", "/containerTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.PATCH, "/containerTypes*", "/containerTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.PUT, "/containerTypes*", "/containerTypes/**").hasAnyAuthority("CREATE_TYPES")
                    .antMatchers(HttpMethod.DELETE, "/containerTypes*", "/containerTypes/**").hasAnyAuthority("CREATE_TYPES")

                    .antMatchers(HttpMethod.GET, "/cts*", "/cts/**").hasAnyAuthority("CREATE_CT", "READ_CT")
                    .antMatchers(HttpMethod.POST, "/cts*", "/cts/**").hasAnyAuthority("CREATE_CT")
                    .antMatchers(HttpMethod.PATCH, "/cts*", "/cts/**").hasAnyAuthority("CREATE_CT")
                    .antMatchers(HttpMethod.PUT, "/cts*", "/cts/**").hasAnyAuthority("CREATE_CT", "UPDATE_CT")
                    .antMatchers(HttpMethod.DELETE, "/cts*", "/cts/**").hasAnyAuthority("CREATE_CT")


                    .antMatchers(HttpMethod.GET, "/continuousQueries*", "/v/**").hasAnyAuthority("CREATE_METRIC", "READ_METRIC")
                    .antMatchers(HttpMethod.POST, "/continuousQueries*", "/continuousQueries/**").hasAnyAuthority("CREATE_METRIC")
                    .antMatchers(HttpMethod.PATCH, "/continuousQueries*", "/continuousQueries/**").hasAnyAuthority("CREATE_METRIC")
                    .antMatchers(HttpMethod.PUT, "/continuousQueries*", "/continuousQueries/**").hasAnyAuthority("CREATE_METRIC", "UPDATE_METRIC")
                    .antMatchers(HttpMethod.DELETE, "/continuousQueries*", "/continuousQueries/**").hasAnyAuthority("CREATE_METRIC")


                    .antMatchers(HttpMethod.GET, "/locations*", "/locations/**").hasAnyAuthority("CREATE_LOCATION", "READ_LOCATION")
                    .antMatchers(HttpMethod.POST, "/locations*", "/locations/**").hasAnyAuthority("CREATE_LOCATION")
                    .antMatchers(HttpMethod.PATCH, "/locations*", "/locations/**").hasAnyAuthority("CREATE_LOCATION")
                    .antMatchers(HttpMethod.PUT, "/locations*", "/locations/**").hasAnyAuthority("CREATE_LOCATION", "UPDATE_LOCATION")
                    .antMatchers(HttpMethod.DELETE, "/locations*", "/locations/**").hasAnyAuthority("CREATE_LOCATION")

                    .antMatchers(HttpMethod.GET, "/collectors*", "/collectors/**").hasAnyAuthority("CREATE_COLLECTOR", "READ_COLLECTOR")
                    .antMatchers(HttpMethod.POST, "/collectors*", "/collectors/**").hasAnyAuthority("CREATE_COLLECTOR")
                    .antMatchers(HttpMethod.PATCH, "/collectors*", "/collectors/**").hasAnyAuthority("CREATE_COLLECTOR")
                    .antMatchers(HttpMethod.PUT, "/collectors*", "/collectors/**").hasAnyAuthority("CREATE_COLLECTOR", "UPDATE_COLLECTOR")
                    .antMatchers(HttpMethod.DELETE, "/collectors*", "/collectors/**").hasAnyAuthority("CREATE_COLLECTOR")

                    .antMatchers(HttpMethod.GET, "/managers*", "/managers/**").hasAnyAuthority("CREATE_MANAGER", "READ_MANAGER")
                    .antMatchers(HttpMethod.POST, "/managers*", "/managers/**").hasAnyAuthority("CREATE_MANAGER")
                    .antMatchers(HttpMethod.PATCH, "/managers*", "/managers/**").hasAnyAuthority("CREATE_MANAGER")
                    .antMatchers(HttpMethod.PUT, "/managers*", "/managers/**").hasAnyAuthority("CREATE_MANAGER", "UPDATE_MANAGER")
                    .antMatchers(HttpMethod.DELETE, "/managers*", "/managers/**").hasAnyAuthority("CREATE_MANAGER")

                    .antMatchers(HttpMethod.GET, "/userAccounts*", "/userAccounts/**").hasAnyAuthority("CREATE_USER", "READ_USER")
                    .antMatchers(HttpMethod.POST, "/userAccounts*", "/userAccounts/**").hasAnyAuthority("CREATE_USER")
                    .antMatchers(HttpMethod.PATCH, "/userAccounts*", "/userAccounts/**").hasAnyAuthority("CREATE_USER")
                    .antMatchers(HttpMethod.PUT, "/users*", "/users/**").hasAnyAuthority("CREATE_USER", "UPDATE_USER")
                    .antMatchers(HttpMethod.DELETE, "/users*", "/users/**").hasAnyAuthority("CREATE_USER")

                    .antMatchers(HttpMethod.GET, "/ldap*", "/ldap/**").hasAnyAuthority("CREATE_USER", "READ_USER")
                    .antMatchers(HttpMethod.POST, "/ldap*", "/ldap/**").hasAnyAuthority("CREATE_USER")
                    .antMatchers(HttpMethod.PATCH, "/ldap*", "/ldap/**").hasAnyAuthority("CREATE_USER")
                    .antMatchers(HttpMethod.PUT, "/ldap*", "/ldap/**").hasAnyAuthority("CREATE_USER", "UPDATE_USER")
                    .antMatchers(HttpMethod.DELETE, "/ldap*", "/ldap/**").hasAnyAuthority("CREATE_USER")


                    .antMatchers(HttpMethod.GET, "/roles*", "/roles/**").hasAnyAuthority("CREATE_ROLE", "READ_ROLE")
                    .antMatchers(HttpMethod.POST, "/roles*", "/roles/**").hasAnyAuthority("CREATE_ROLE")
                    .antMatchers(HttpMethod.PATCH, "/roles*", "/roles/**").hasAnyAuthority("CREATE_ROLE")
                    .antMatchers(HttpMethod.PUT, "/roles*", "/roles/**").hasAnyAuthority("CREATE_ROLE", "UPDATE_ROLE")
                    .antMatchers(HttpMethod.DELETE, "/roles*", "/roles/**").hasAnyAuthority("CREATE_ROLE")

                    .antMatchers(HttpMethod.GET, "/privileges*", "/privileges/**").hasAnyAuthority("CREATE_PRIVILEGE", "READ_PRIVILEGE")
                    .antMatchers(HttpMethod.POST, "/privileges*", "/privileges/**").hasAnyAuthority("CREATE_PRIVILEGE")
                    .antMatchers(HttpMethod.PATCH, "/privileges*", "/privileges/**").hasAnyAuthority("CREATE_PRIVILEGE")
                    .antMatchers(HttpMethod.PUT, "/privileges*", "/privileges/**").hasAnyAuthority("CREATE_PRIVILEGE", "UPDATE_PRIVILEGE")
                    .antMatchers(HttpMethod.DELETE, "/privileges*", "/privileges/**").hasAnyAuthority("CREATE_PRIVILEGE")

                    .antMatchers(HttpMethod.GET, "/workgroups*", "/workgroups/**").hasAnyAuthority("CREATE_WORKGROUP", "READ_WORKGROUP")
                    .antMatchers(HttpMethod.POST, "/workgroups*", "/workgroups/**").hasAnyAuthority("CREATE_WORKGROUP")
                    .antMatchers(HttpMethod.PATCH, "/workgroups*", "/workgroups/**").hasAnyAuthority("CREATE_WORKGROUP")
                    .antMatchers(HttpMethod.PUT, "/workgroups*", "/workgroups/**").hasAnyAuthority("CREATE_WORKGROUP", "UPDATE_WORKGROUP")
                    .antMatchers(HttpMethod.DELETE, "/workgroups*", "/workgroups/**").hasAnyAuthority("CREATE_WORKGROUP")

                    .antMatchers(HttpMethod.GET, "/containers*", "/containers/**").hasAnyAuthority("CREATE_CONTAINER", "READ_CONTAINER")
                    .antMatchers(HttpMethod.POST, "/containers*", "/containers/**").hasAnyAuthority("CREATE_CONTAINER")
                    .antMatchers(HttpMethod.PATCH, "/containers*", "/containers/**").hasAnyAuthority("CREATE_CONTAINER")
                    .antMatchers(HttpMethod.PUT, "/containers*", "/containers/**").hasAnyAuthority("CREATE_CONTAINER", "UPDATE_CONTAINER")
                    .antMatchers(HttpMethod.DELETE, "/containers*", "/containers/**").hasAnyAuthority("CREATE_CONTAINER")

                    .antMatchers(HttpMethod.GET, "/companies*", "/companies/**").hasAnyAuthority("CREATE_COMPANY", "READ_COMPANY")
                    .antMatchers(HttpMethod.POST, "/companies*", "/companies/**").hasAnyAuthority("CREATE_COMPANY")
                    .antMatchers(HttpMethod.PATCH, "/companies*", "/companies/**").hasAnyAuthority("CREATE_COMPANY")
                    .antMatchers(HttpMethod.PUT, "/companies*", "/companies/**").hasAnyAuthority("CREATE_COMPANY", "UPDATE_COMPANY")
                    .antMatchers(HttpMethod.DELETE, "/companies*", "/companies/**").hasAnyAuthority("CREATE_COMPANY")

                    .antMatchers(HttpMethod.GET, "/contacts*", "/contacts/**").hasAnyAuthority("CREATE_CONTACT", "READ_CONTACT")
                    .antMatchers(HttpMethod.POST, "/contacts*", "/contacts/**").hasAnyAuthority("CREATE_CONTACT")
                    .antMatchers(HttpMethod.PATCH, "/contacts*", "/contacts/**").hasAnyAuthority("CREATE_CONTACT")
                    .antMatchers(HttpMethod.PUT, "/contacts*", "/contacts/**").hasAnyAuthority("CREATE_CONTACT", "UPDATE_CONTACT")
                    .antMatchers(HttpMethod.DELETE, "/contacts*", "/contacts/**").hasAnyAuthority("CREATE_CONTACT")

                    .antMatchers(HttpMethod.GET, "/monitors*", "/monitors/**").hasAnyAuthority("CREATE_MONITOR", "READ_MONITOR")
                    .antMatchers(HttpMethod.POST, "/monitors*", "/monitors/**").hasAnyAuthority("CREATE_MONITOR")
                    .antMatchers(HttpMethod.PATCH, "/monitors*", "/monitors/**").hasAnyAuthority("CREATE_MONITOR")
                    .antMatchers(HttpMethod.PUT, "/monitors*", "/monitors/**").hasAnyAuthority("CREATE_MONITOR", "UPDATE_MONITOR")
                    .antMatchers(HttpMethod.DELETE, "/monitors*", "/monitors/**").hasAnyAuthority("CREATE_MONITOR")

                    .antMatchers(HttpMethod.GET, "/metrics*", "/metrics/**").hasAnyAuthority("CREATE_METRIC", "READ_METRIC")
                    .antMatchers(HttpMethod.POST, "/metrics*", "/metrics/**").hasAnyAuthority("CREATE_METRIC")
                    .antMatchers(HttpMethod.PATCH, "/metrics*", "/metrics/**").hasAnyAuthority("CREATE_METRIC")
                    .antMatchers(HttpMethod.PUT, "/metrics*", "/metrics/**").hasAnyAuthority("CREATE_METRIC", "UPDATE_METRIC")
                    .antMatchers(HttpMethod.DELETE, "/metrics*", "/metrics/**").hasAnyAuthority("CREATE_METRIC")

                    .antMatchers(HttpMethod.GET, "/eventrules*", "/eventrules/**").hasAnyAuthority("CREATE_EVENTRULE", "READ_EVENTRULE")
                    .antMatchers(HttpMethod.POST, "/eventrules*", "/eventrules/**").hasAnyAuthority("CREATE_EVENTRULE")
                    .antMatchers(HttpMethod.PATCH, "/eventrules*", "/eventrules/**").hasAnyAuthority("CREATE_EVENTRULE")
                    .antMatchers(HttpMethod.PUT, "/eventrules*", "/eventrules/**").hasAnyAuthority("CREATE_EVENTRULE", "UPDATE_EVENTRULE")
                    .antMatchers(HttpMethod.DELETE, "/eventrules*", "/eventrules/**").hasAnyAuthority("CREATE_EVENTRULE")

                    .antMatchers(HttpMethod.GET, "/instrumentations*", "/instrumentations/**").hasAnyAuthority("CREATE_INSTRUMENTATION", "READ_INSTRUMENTATION")
                    .antMatchers(HttpMethod.POST, "/instrumentations*", "/instrumentations/**").hasAnyAuthority("CREATE_INSTRUMENTATION")
                    .antMatchers(HttpMethod.PATCH, "/instrumentations*", "/instrumentations/**").hasAnyAuthority("CREATE_INSTRUMENTATION")
                    .antMatchers(HttpMethod.PUT, "/instrumentations*", "/instrumentations/**").hasAnyAuthority("CREATE_INSTRUMENTATION", "UPDATE_INSTRUMENTATION")
                    .antMatchers(HttpMethod.DELETE, "/instrumentations*", "/instrumentations/**").hasAnyAuthority("CREATE_INSTRUMENTATION")

                    .antMatchers(HttpMethod.GET, "/dashboards*", "/dashboards/**").hasAnyAuthority("CREATE_DASHBOARD", "READ_DASHBOARD")
                    .antMatchers(HttpMethod.POST, "/dashboards*", "/dashboards/**").hasAnyAuthority("CREATE_DASHBOARD")
                    .antMatchers(HttpMethod.PATCH, "/dashboards*", "/dashboards/**").hasAnyAuthority("CREATE_DASHBOARD")
                    .antMatchers(HttpMethod.PUT, "/dashboards*", "/dashboards/**").hasAnyAuthority("CREATE_DASHBOARD", "UPDATE_DASHBOARD")
                    .antMatchers(HttpMethod.DELETE, "/dashboards*", "/dashboards/**").hasAnyAuthority("CREATE_DASHBOARD")

                    .antMatchers(HttpMethod.GET, "/dashboard*", "/dashboardUsers/**", "/monitorCtStatus*", "/monitorCtStatuses/**").hasAnyAuthority("CREATE_DASHBOARD", "READ_DASHBOARD")
                    .antMatchers(HttpMethod.POST, "/dashboard*", "/dashboardUsers/**", "/monitorCtStatus*", "/monitorCtStatuses/**").hasAnyAuthority("CREATE_DASHBOARD")
                    .antMatchers(HttpMethod.PATCH, "/dashboard*", "/dashboardUsers/**", "/monitorCtStatus*", "/monitorCtStatuses/**").hasAnyAuthority("CREATE_DASHBOARD")
                    .antMatchers(HttpMethod.PUT, "/dashboard*", "/dashboardUsers/**", "/monitorCtStatus*", "/monitorCtStatuses/**").hasAnyAuthority("CREATE_DASHBOARD", "UPDATE_DASHBOARD")
                    .antMatchers(HttpMethod.DELETE, "/dashboard*", "/dashboardUsers/**", "/monitorCtStatus*", "/monitorCtStatuses/**").hasAnyAuthority("CREATE_DASHBOARD")


                    .antMatchers(HttpMethod.GET, "/dynamicsearches*", "/dynamicsearches/**").hasAnyAuthority("CREATE_DYNAMICSEARCH", "READ_DYNAMICSEARCH")
                    .antMatchers(HttpMethod.POST, "/dynamicsearches*", "/dynamicsearches/**").hasAnyAuthority("CREATE_DYNAMICSEARCH")
                    .antMatchers(HttpMethod.PATCH, "/dynamicsearches*", "/dynamicsearches/**").hasAnyAuthority("CREATE_DYNAMICSEARCH")
                    .antMatchers(HttpMethod.PUT, "/dynamicsearches*", "/dynamicsearches/**").hasAnyAuthority("CREATE_DYNAMICSEARCH", "UPDATE_DYNAMISEARCH")
                    .antMatchers(HttpMethod.DELETE, "/dynamicsearches*", "/dynamicsearches/**").hasAnyAuthority("CREATE_DYNAMICSEARCH")

                    .antMatchers(HttpMethod.GET, "/monitoringprofiles*", "/monitoringprofiles/**").hasAnyAuthority("CREATE_MONITORINGPROFILE", "READ_MONITORINGPROFILE")
                    .antMatchers(HttpMethod.POST, "/monitoringprofiles*", "/monitoringprofiles/**").hasAnyAuthority("CREATE_MONITORINGPROFILE")
                    .antMatchers(HttpMethod.PATCH, "/monitoringprofiles*", "/monitoringprofiles/**").hasAnyAuthority("CREATE_MONITORINGPROFILE")
                    .antMatchers(HttpMethod.PUT, "/monitoringprofiles*", "/monitoringprofiles/**").hasAnyAuthority("CREATE_MONITORINGPROFILE", "UPDATE_MONITORINGPROFILE")
                    .antMatchers(HttpMethod.DELETE, "/monitoringprofiles*", "/monitoringprofiles/**").hasAnyAuthority("CREATE_MONITORINGPROFILE")

                    .antMatchers(HttpMethod.GET, "/outages*", "/outages/**").hasAnyAuthority("CREATE_OUTAGE", "READ_OUTAGE")
                    .antMatchers(HttpMethod.POST, "/outages*", "/outages/**").hasAnyAuthority("CREATE_OUTAGE")
                    .antMatchers(HttpMethod.PATCH, "/outages*", "/outages/**").hasAnyAuthority("CREATE_OUTAGE")
                    .antMatchers(HttpMethod.PUT, "/outages*", "/outages/**").hasAnyAuthority("CREATE_OUTAGE", "UPDATE_OUTAGE")
                    .antMatchers(HttpMethod.DELETE, "/outages*", "/outages/**").hasAnyAuthority("CREATE_OUTAGE")

                    .antMatchers(HttpMethod.GET, "/reset*", "/reset/**").hasAnyAuthority("EXEC_RESET")
                    .antMatchers(HttpMethod.POST, "/reset*", "/reset/**").hasAnyAuthority("EXEC_RESET")
                    .antMatchers(HttpMethod.PATCH, "/reset*", "/reset/**").hasAnyAuthority("EXEC_RESET")
                    .antMatchers(HttpMethod.PUT, "/reset*", "/reset/**").hasAnyAuthority("EXEC_RESET")
                    .antMatchers(HttpMethod.DELETE, "/reset*", "/reset/**").hasAnyAuthority("EXEC_RESET")

                    .antMatchers(HttpMethod.GET, "/disable*", "/disable/**").hasAnyAuthority("EXEC_DISABLE")
                    .antMatchers(HttpMethod.POST, "/disable*", "/disable/**").hasAnyAuthority("EXEC_DISABLE")
                    .antMatchers(HttpMethod.PATCH, "/disable*", "/disable/**").hasAnyAuthority("EXEC_DISABLE")
                    .antMatchers(HttpMethod.PUT, "/disable*", "/disable/**").hasAnyAuthority("EXEC_DISABLE")
                    .antMatchers(HttpMethod.DELETE, "/disable*", "/disable/**").hasAnyAuthority("EXEC_DISABLE")

                    .antMatchers(HttpMethod.GET, "/operate*", "/operate/**").hasAnyAuthority("EXEC_OPERATE")
                    .antMatchers(HttpMethod.POST, "/operate*", "/operate/**").hasAnyAuthority("EXEC_OPERATE")
                    .antMatchers(HttpMethod.PATCH, "/operate*", "/operate/**").hasAnyAuthority("EXEC_OPERATE")
                    .antMatchers(HttpMethod.PUT, "/operate*", "/operate/**").hasAnyAuthority("EXEC_OPERATE")
                    .antMatchers(HttpMethod.DELETE, "/operate*", "/operate/**").hasAnyAuthority("EXEC_OPERATE")

                    .antMatchers(HttpMethod.GET, "/dispose*", "/dispose/**").hasAnyAuthority("EXEC_DISPOSE")
                    .antMatchers(HttpMethod.POST, "/dispose*", "/dispose/**").hasAnyAuthority("EXEC_DISPOSE")
                    .antMatchers(HttpMethod.PATCH, "/dispose*", "/dispose/**").hasAnyAuthority("EXEC_DISPOSE")
                    .antMatchers(HttpMethod.PUT, "/dispose*", "/dispose/**").hasAnyAuthority("EXEC_DISPOSE")
                    .antMatchers(HttpMethod.DELETE, "/dispose*", "/dispose/**").hasAnyAuthority("EXEC_DISPOSE")

                    .antMatchers(HttpMethod.GET, "/enable*", "/enable/**").hasAnyAuthority("EXEC_ENABLE")
                    .antMatchers(HttpMethod.POST, "/enable*", "/enable/**").hasAnyAuthority("EXEC_ENABLE")
                    .antMatchers(HttpMethod.PATCH, "/enable*", "/enable/**").hasAnyAuthority("EXEC_ENABLE")
                    .antMatchers(HttpMethod.PUT, "/enable*", "/enable/**").hasAnyAuthority("EXEC_ENABLE")
                    .antMatchers(HttpMethod.DELETE, "/enable*", "/enable/**").hasAnyAuthority("EXEC_ENABLE")

                    .antMatchers(HttpMethod.GET, "/attend*", "/attend/**").hasAnyAuthority("EXEC_ATTEND")
                    .antMatchers(HttpMethod.POST, "/attend*", "/attend/**").hasAnyAuthority("EXEC_ATTEND")
                    .antMatchers(HttpMethod.PATCH, "/attend*", "/attend/**").hasAnyAuthority("EXEC_ATTEND")
                    .antMatchers(HttpMethod.PUT, "/attend*", "/attend/**").hasAnyAuthority("EXEC_ATTEND")
                    .antMatchers(HttpMethod.DELETE, "/attend*", "/attend/**").hasAnyAuthority("EXEC_ATTEND")

                    .antMatchers(HttpMethod.POST, "/createTicket*", "/createTicket/**").hasAnyAuthority("EXEC_ATTEND")

                    .antMatchers(HttpMethod.GET, "/history*", "/history/**").hasAnyAuthority("EXEC_HISTORY")
                    .antMatchers(HttpMethod.POST, "/history*", "/history/**").hasAnyAuthority("EXEC_HISTORY")
                    .antMatchers(HttpMethod.PATCH, "/history*", "/history/**").hasAnyAuthority("EXEC_HISTORY")
                    .antMatchers(HttpMethod.PUT, "/history*", "/history/**").hasAnyAuthority("EXEC_HISTORY")
                    .antMatchers(HttpMethod.DELETE, "/history*", "/history/**").hasAnyAuthority("EXEC_HISTORY")

                    .antMatchers(HttpMethod.GET, "/tools*", "/tools/**", "/runtool*", "/runtool/**").hasAnyAuthority("CREATE_TOOL", "EXEC_TOOL", "READ_TOOL")
                    .antMatchers(HttpMethod.POST, "/tools*", "/tools/**", "/runtool*", "/runtool/**").hasAnyAuthority("EXEC_TOOL")
                    .antMatchers(HttpMethod.PATCH, "/tools*", "/tools/**", "/runtool*", "/runtool/**").hasAnyAuthority("EXEC_TOOL")
                    .antMatchers(HttpMethod.PUT, "/tools*", "/tools/**", "/runtool*", "/runtool/**").hasAnyAuthority("EXEC_TOOL", "UPDATE_TOOL")
                    .antMatchers(HttpMethod.DELETE, "/tools*", "/tools/**", "/runtool*", "/runtool/**").hasAnyAuthority("EXEC_TOOL")

                    .antMatchers(HttpMethod.GET, "/tasks*", "/tasks/**", "/runTask*", "/runTask/**").hasAnyAuthority("CREATE_TOOL", "EXEC_TOOL", "READ_TOOL")
                    .antMatchers(HttpMethod.POST, "/tasks*", "/tasks/**", "/runTask*", "/runTask/**").hasAnyAuthority("EXEC_TOOL")
                    .antMatchers(HttpMethod.PATCH, "/tasks*", "/tasks/**", "/runTask*", "/runTask/**").hasAnyAuthority("EXEC_TOOL")
                    .antMatchers(HttpMethod.PUT, "/tasks*", "/tasks/**", "/runTask*", "/runTask/**").hasAnyAuthority("EXEC_TOOL", "UPDATE_TOOL")
                    .antMatchers(HttpMethod.DELETE, "/tasks*", "/tasks/**", "/runTask*", "/runTask/**").hasAnyAuthority("EXEC_TOOL")

                    .antMatchers(HttpMethod.GET, "/taskLog*", "/taskLogs/**").hasAnyAuthority("CREATE_TOOL", "EXEC_TOOL", "READ_TOOL")
                    .antMatchers(HttpMethod.POST, "/taskLog*", "/taskLogs/**").hasAnyAuthority("EXEC_TOOL")
                    .antMatchers(HttpMethod.PATCH, "/taskLog*", "/taskLogs/**").hasAnyAuthority("EXEC_TOOL")
                    .antMatchers(HttpMethod.PUT, "/tasksLog", "/taskLogs/**").hasAnyAuthority("EXEC_TOOL", "UPDATE_TOOL")
                    .antMatchers(HttpMethod.DELETE, "/taskLog*", "/taskLogs/**").hasAnyAuthority("EXEC_TOOL")

                    .antMatchers(HttpMethod.GET, "/collectorConfig/*").permitAll()

                    // all other requests need to be authenticated
                    .anyRequest().authenticated().and()

                    .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

            // Add a filter to validate the tokens with every request
            return httpSecurity.authenticationProvider(authenticationProvider()).addFilterAfter(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class).build();

        } else {
            return httpSecurity.csrf().disable().build();
        }
    }

}
