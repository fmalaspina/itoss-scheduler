package com.frsi.itoss.mgr.controllers;

import com.frsi.itoss.mgr.security.AuthenticationFacade;
import com.frsi.itoss.mgr.services.CtService;
import com.frsi.itoss.model.commonservices.ItossdbDAOServices;
import com.frsi.itoss.model.company.Contact;
import com.frsi.itoss.model.ct.Ct;
import com.frsi.itoss.model.ct.CtBulkAdapter;
import com.frsi.itoss.model.ct.CtPasswordHandler;
import com.frsi.itoss.model.repository.*;
import com.frsi.itoss.model.tennant.Tennant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.ValidationException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

@RestController
@RestResource
@RequestMapping("/cts")

public class CtController {


    @Value("${itoss.defaultTennantId:0}")
    Long defaultTennantId;
    @Autowired
    AuthenticationFacade auth;
    @Autowired
    CtService resetCtService;
    @Autowired
    DashboardEntryRepo dashboardEntryRepo;
    @Autowired
    CtRepo ctRepo;

    @Autowired
    ItossdbDAOServices itossdbDaoServices;
    @Autowired
    LocationRepo locationRepo;
    @Autowired
    CtStatusRepo ctStatusRepo;
    @Autowired
    CollectorRepo collectorRepo;
    @Autowired
    ContactRepo contactRepo;
    @Autowired
    CompanyRepo companyRepo;
    @Autowired
    MonitoringProfileRepo monitoringProfileRepo;
    @Autowired
    CtRelationRepo ctRelationRepo;
    @Autowired
    UserAccountRepo userAccountRepo;
    @Autowired
    TennantRepo tennantRepo;
    @Autowired
    CtTypeRepo ctTypeRepo;
    @Autowired
    CtPasswordHandler ctPasswordHanlder;
    @Autowired
    WorkgroupRepo workgroupRepo;


    @GetMapping(value = {"/{id}/validate"}, produces = "application/json")
    public ResponseEntity<?> validate(@PathVariable(required = true) Long id) {
        Optional<Ct> ct = ctRepo.findById(id);

        if (ct.isPresent()) {
            final Ct ctFinal = ct.get();

            return ResponseEntity.ok(ctFinal.validateAttributes());
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @Transactional
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?> save(@RequestBody CtBulkAdapter ctBulkAdapter) {

        Ct ct = new Ct();
        try {

            if (ctBulkAdapter.getAttributes() != null)
                ct.setAttributes(ctBulkAdapter.getAttributes());
            if (ctBulkAdapter.getCollector() != null)
                ct.setCollector(collectorRepo.findById(ctBulkAdapter.getCollector().getId()).get());
            if (ctBulkAdapter.getCompany() != null)
                ct.setCompany(companyRepo.findById(ctBulkAdapter.getCompany().getId()).get());
            if (ctBulkAdapter.getContact() != null)
                ct.setContact(contactRepo.findById(ctBulkAdapter.getContact().getId()).get());
            if (ctBulkAdapter.getEnvironment() != null)
                ct.setEnvironment(ctBulkAdapter.getEnvironment());
            if (ctBulkAdapter.getInstrumentationParameterValues() != null)
                ct.setInstrumentationParameterValues(ctBulkAdapter.getInstrumentationParameterValues());
            if (ctBulkAdapter.getLocation() != null)
                ct.setLocation(locationRepo.findById(ctBulkAdapter.getLocation().getId()).get());
            if (ctBulkAdapter.getMonitoringProfile() != null)
                ct.setMonitoringProfile(
                        monitoringProfileRepo.findById(ctBulkAdapter.getMonitoringProfile().getId()).get());
            if (ctBulkAdapter.getSupportUser() != null)
                ct.setSupportUser(userAccountRepo.findById(ctBulkAdapter.getSupportUser().getId()).get());
            if (ctBulkAdapter.getType() != null)
                ct.setType(ctTypeRepo.findById(ctBulkAdapter.getType().getId()).get());
            if (ctBulkAdapter.getWorkgroup() != null)
                ct.setWorkgroup(workgroupRepo.findById(ctBulkAdapter.getWorkgroup().getId()).get());
            if (ctBulkAdapter.getIntegrationId() != null)
                ct.setIntegrationId(ctBulkAdapter.getIntegrationId());
            ct = ctPasswordHanlder.handleCtCreate(ct);

            final Ct ctFinal = ctRepo.save(ct);

            ctBulkAdapter.getTennantIds().forEach(t -> {
                Optional<Tennant> tennant = tennantRepo.findById(t);
                if (tennant.isPresent()) {
                    Tennant te = tennant.get();
                    te.getCts().add(ctFinal);
                    tennantRepo.save(te);
                } else {
                    throw new RuntimeException("The Tennant Id:" + t + " was not found.");
                }
            });
            Optional<Tennant> defaultTennant = tennantRepo.findById(defaultTennantId);
            if (defaultTennant.isPresent()) {
                Tennant t = defaultTennant.get();
                if (!t.getCts().contains(ctFinal)) {
                    t.getCts().add(ctFinal);
                    tennantRepo.save(t);
                }
            }

        } catch (Exception e) {
            throw new ValidationException(e.getMessage());
        }
        return ResponseEntity.ok(ct);

    }


    @GetMapping(value = {"/{id}/reset"}, produces = "application/json")
    public ResponseEntity<?> reset(@PathVariable(required = true) Long id) throws TimeoutException {
        resetCtService.reset(id);
        return ResponseEntity.ok().build();

    }


    @RequestMapping(value = "/search/counterStateByUserId", method = RequestMethod.GET, produces = "application/json")
    public List<Map<String, Object>> findCtsCountByUserId(@RequestParam("userId") Long userId) throws Exception {


        String queryStr = """
                WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = :userId) 
                SELECT c.state, count(*) as count  FROM ct c WHERE c.id IN (SELECT cts_id  FROM tennant) GROUP BY c.state
                """;
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        return itossdbDaoServices.queryForList(queryStr, params);

    }

    @RequestMapping(value = "/search/counterEnvironmentByUserIdAndState", method = RequestMethod.GET, produces = "application/json")
    public List<Map<String, Object>> findCtsCount(@RequestParam("userId") Long userId, @RequestParam("state") String state) throws Exception {


        String queryStr = """
                WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = :userId) 
                SELECT c.environment, count(*) as quantity FROM ct c WHERE c.id IN (SELECT cts_id FROM tennant) AND c.state = :state GROUP BY c.environment
                """;
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("state", state);
        return itossdbDaoServices.queryForList(queryStr, params);

    }

    @RequestMapping(value = "/search/findLastStateChangeByUser", method = RequestMethod.GET, produces = "application/json")
    public List<Map<String, Object>> findLastStatusChangeByUser(@RequestParam("userId") Long userId)
            throws Exception {


        var queryStr = """
                 WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = :userId)
                  SELECT c.name, c.environment, c.state, co.name AS company_name, cth.event AS last_event, cth.creation_date AS last_event_date 
                  FROM ct c INNER JOIN company co ON c.company_id = co.id 
                  INNER JOIN ct_history cth ON cth.ct_id = c.id AND cth.id = (SELECT MAX(cthm.id) FROM ct_history cthm WHERE cthm.ct_id = c.id AND cthm.successful = true
                   AND cthm.event in ('OPERATE', 'ENABLE', 'DISABLE', 'DISPOSE', 'OUTAGE_START', 'OUTAGE_END'))
                    WHERE c.id IN (SELECT cts_id FROM tennant) ORDER BY 6
                """;
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        return itossdbDaoServices.queryForList(queryStr, params);

    }

    @RequestMapping(value = "/search/findCtsByUserIdAndCtName", method = RequestMethod.GET, produces = "application/json")
    public List<Map<String, Object>> findCtsByUserAndCtName(@RequestParam("userId") Long userId,
                                                            @RequestParam("ctName") String ctName) throws SQLException, Exception {

        String queryStr = """
                WITH tennant AS (SELECT cts_id FROM tennant_cts tc 
                INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = :userId) 
                (SELECT DISTINCT 'COMPANY' as type, co.id, co.name, l.name small_info 
                 FROM ct c  
                 INNER JOIN company co ON c.company_id = co.id 
                 LEFT JOIN location l ON l.id = co.location_id   
                 WHERE c.id IN (SELECT cts_id  FROM tennant) 
                    AND co.name ILIKE CONCAT('%',:ctName, '%') 
                 ORDER BY co.name ASC LIMIT 25) 
                  
                 UNION ALL 
                 (SELECT DISTINCT 'CT' as type, c.id, c.name, ct.name small_info 
                 FROM ct c 
                 INNER JOIN ct_type ct ON ct.id = c.type_id 
                 INNER JOIN company co ON c.company_id = co.id 
                 WHERE c.id IN 
                 (SELECT cts_id FROM tennant) 
                      AND c.name ILIKE CONCAT('%', :ctName, '%') 
                      ORDER BY c.name ASC LIMIT 25)
                """;
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("ctName", ctName);
        return itossdbDaoServices.queryForList(queryStr, params);

    }


    @GetMapping("/countByState")
    public ResponseEntity<?> countByState() {
        var user = auth.getUserAccount();
        return ResponseEntity.ok(ctRepo.countByStateForUserId(user.getId()));
    }


}
