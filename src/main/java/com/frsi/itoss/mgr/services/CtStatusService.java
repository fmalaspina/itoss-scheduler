package com.frsi.itoss.mgr.services;

import com.frsi.itoss.model.commonservices.ItossdbDAOServices;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class CtStatusService {

    private final ItossdbDAOServices itossdbDAOServices;

    public CtStatusService(ItossdbDAOServices itossdbDAOServices) {
        this.itossdbDAOServices = itossdbDAOServices;
    }

    public List<Map<String, Object>> getCTStatusTree(Long userId, String environment, Long supportUserId) throws Exception {

//        String query =
//                "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = + " + userId + ")" +
//                        " select l.location_id,l.location_name,l.location_parent_id,l.location_parent_name,\n" +
//                        " l.type_id," +
//                        " l.type_name," +
//                        " l.type_path," +
//                        " sum(l.down_nodes)::integer down_nodes," +
//                        " sum(l.up_nodes)::integer up_nodes " +
//                        " from (SELECT lo.id location_id, lo.name location_name, lo.parent_id location_parent_id, lp.name location_parent_name, ctt.id type_id, ctt.name type_name, ctt.type_path, cts.down, " +
//                        "case when cts.down = true then count(*) else 0 end down_nodes, " +
//                        " case when cts.down = false then count(*) else 0 end up_nodes FROM ct ct INNER JOIN ct_type ctt on ctt.id = ct.type_id INNER JOIN ct_status cts on cts.id = ct.id   INNER JOIN location lo on lo.id = ct.location_id " +
//                        " INNER JOIN location lp on lp.id = lo.parent_id " +
//                        " WHERE ct.id IN (SELECT cts_id FROM tennant) AND ('" + environment + "' = '' OR ct.environment = ANY (select unnest(string_to_array('" + environment + "', ',')))) AND (" + supportUserId + " = 0 OR ct.support_user_id = " + supportUserId + ") AND ct.state = 'OPERATIONS' GROUP BY lo.id, lo.name, lp.name, ctt.id, ctt.name, ctt.type_path, cts.down ORDER BY lo.name,ctt.name) as l group by l.location_id,l.location_name,l.location_parent_id,l.location_parent_name," +
//                        "l.type_id," +
//                        "l.type_name," +
//                        "l.type_path";


        String query = """
                WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = :userId) 
                                              
                                                    SELECT lo.id location_id, lo.name location_name, lo.parent_id location_parent_id, lp.name location_parent_name, lt.name location_type,
                                                   
                                                     ctt.id type_id, ctt.name type_name, ctt.type_path,
                                                   
                                                           COUNT(CASE WHEN cts.down = true THEN 1 END)::integer AS down_nodes,
                                                   
                                                           COUNT(CASE WHEN cts.down = false THEN 1 END)::integer AS up_nodes
                                                   
                                                     FROM  location lo
                                                   
                                                           LEFT JOIN location lp ON lp.id = lo.parent_id
                                                           LEFT JOIN location_type lt ON lt.id = lo.type_id
                                                           LEFT JOIN ct ct ON ct.location_id = lo.id
                                                   
                                                              AND ct.id IN (SELECT cts_id FROM tennant)
                                                   
                                                              AND (:environment = '' OR ct.environment = ANY (select unnest(string_to_array(:environment, ','))))
                                                   
                                                              AND (:supportUserId = 0 OR ct.support_user_id = 0)
                                                   
                                                              AND ct.state = 'OPERATIONS'
                                                   
                                                           LEFT JOIN ct_type ctt ON ctt.id = ct.type_id
                                                   
                                                           LEFT JOIN ct_status cts ON cts.id = ct.id  
                                                   
                                                   GROUP BY lo.id, lo.name, lp.name, lt.name, ctt.id, ctt.name, ctt.type_path
                                                   
                                                   ORDER BY lo.id,lo.name, ctt.name 
                """;

        var params = new HashMap<String, Object>();
        params.put("userId", userId);
        params.put("environment", environment);
        params.put("supportUserId", supportUserId);

        var locationList = itossdbDAOServices.queryForList(query, params);


        locationList = mergeWithSameLocation(locationList);


        // copy list to new list
        List<Map<String, Object>> temp = new ArrayList<>(locationList);

        for (int i = 0; i < temp.size(); i++) {
            mergeWithParent(temp.get(i), locationList);
        }
        return locationList;
    }

    // recursive method to add to parent node
    private void mergeWithParent(Map<String, Object> node, List<Map<String, Object>> locationList) {

        if (node.get("location_parent_id") != null && node.get("location_parent_id") != node.get("location_id")) {
            var parent = locationList.stream().filter(e -> e.get("location_id").equals(node.get("location_parent_id"))).findFirst();


            if (parent.isPresent()) {


                var parentFound = parent.get();

                // find child nodes of node
                var hastChildrens = locationList.stream()
                        .filter(e -> e.get("location_parent_id") != null && e.get("location_parent_id").equals(node.get("location_id")))
                        .collect(Collectors.toList()).size() > 0;
                if (!hastChildrens) {
                    parentFound.put("down_nodes", (Integer) parentFound.get("down_nodes") + (Integer) node.get("down_nodes"));
                    parentFound.put("up_nodes", (Integer) parentFound.get("up_nodes") + (Integer) node.get("up_nodes"));
                    parentFound.put("child_locations", parentFound.get("child_locations") == null ? new ArrayList<Map<String, Object>>() : parentFound.get("child_locations"));
                    if ((Integer) node.get("down_nodes") > 0 || (Integer) node.get("up_nodes") > 0) {
                        ((List<Map<String, Object>>) parentFound.get("child_locations")).add(node);

                        //((List<Map<String, Object>>) parentFound.get("child_locations")).add(node);
                        parentFound.put("ct_types", parentFound.get("ct_types") == null ? new ArrayList<Map<String, Object>>() : parentFound.get("ct_types"));
                        var ctTypes = (List<Map<String, Object>>) node.get("ct_types");

                        if (ctTypes != null && ctTypes.size() > 0) {
                            var parentCtTypes = ((List<Map<String, Object>>) parentFound.get("ct_types"));
                            // merge ct types with parent
                            for (int i = 0; i < ctTypes.size(); i++) {
                                var ctType = ctTypes.get(i);
                                var parentCtType = parentCtTypes.stream()
                                        .filter(e -> e.get("type_id") != null && e.get("type_id").equals(ctType.get("type_id"))).findFirst();
                                if (parentCtType.isPresent() && parentCtType.get().get("type_id") != null) {
                                    parentCtType.get().put("down_nodes", (Integer) parentCtType.get().get("down_nodes") + (Integer) ctType.get("down_nodes"));
                                    parentCtType.get().put("up_nodes", (Integer) parentCtType.get().get("up_nodes") + (Integer) ctType.get("up_nodes"));
                                } else {
                                    parentCtTypes.add(ctType);
                                }
                            }
                        }
                    }

                    // find node index within graph
                    try {
                        int index = locationList.indexOf(node);
                        locationList.remove(index);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("node: " + node);
                    }

                    mergeWithParent(parentFound, locationList);


                }
            }

        }
    }


    private List<Map<String, Object>> mergeWithSameLocation(List<Map<String, Object>> graph) {

        for (int i = 0; i < graph.size(); i++) {

            // collapse ct
            // populate CtType record


            var ct_type = new HashMap<String, Object>();
            var ct_types = new ArrayList<Map<String, Object>>();
            // convert map to object


            if (graph.get(i).get("type_id") != null && ((Integer) graph.get(i).get("up_nodes") > 0 || (Integer) graph.get(i).get("down_nodes") > 0)) {


                ct_type.put("type_id", graph.get(i).get("type_id"));
                ct_type.put("type_name", graph.get(i).get("type_name"));
                ct_type.put("type_path", graph.get(i).get("type_path"));
                ct_type.put("down_nodes", graph.get(i).get("down_nodes"));
                ct_type.put("up_nodes", graph.get(i).get("up_nodes"));
                ct_types.add(ct_type);
            }

            graph.get(i).remove("type_id");
            graph.get(i).remove("type_name");
            graph.get(i).remove("type_path");
            graph.get(i).put("ct_types", ct_types);

            // order graph by location_id
            int j = i + 1;
            //for (int j = i + 1; j < graph.size(); j++) {


            //  if (graph.get(j).get("location_id").equals(graph.get(i).get("location_id"))) {

            while (j < graph.size() && graph.get(j).get("location_id").equals(graph.get(i).get("location_id"))) {
                var ct_type_others = new HashMap<String, Object>();
                if (graph.get(j).get("type_id") != null && ((Integer) graph.get(j).get("up_nodes") > 0 || (Integer) graph.get(j).get("down_nodes") > 0)) {
                    // populate record location and ctType

                    ct_type_others.put("type_id", graph.get(j).get("type_id"));
                    ct_type_others.put("type_name", graph.get(j).get("type_name"));
                    ct_type_others.put("type_path", graph.get(j).get("type_path"));
                    ct_type_others.put("down_nodes", graph.get(j).get("down_nodes"));
                    ct_type_others.put("up_nodes", graph.get(j).get("up_nodes"));
                    if (!graph.get(j).get("type_id").equals(ct_type.get("type_id"))) {

                        ct_types.add(ct_type_others);

                        graph.get(i).put("down_nodes", (Integer) graph.get(j).get("down_nodes") + (Integer) graph.get(i).get("down_nodes"));
                        graph.get(i).put("up_nodes", (Integer) graph.get(j).get("up_nodes") + (Integer) graph.get(i).get("up_nodes"));

                    } else {
                        ct_types.stream().filter(ct -> ct.get("type_id").equals(ct_type_others.get("type_id"))).findFirst().ifPresent(ct -> {
                            ct.put("down_nodes", (Integer) ct_type_others.get("down_nodes") + (Integer) ct.get("down_nodes"));
                            ct.put("up_nodes", (Integer) ct_type_others.get("up_nodes") + (Integer) ct.get("up_nodes"));
                        });


                    }
                }

                graph.remove(j);
                //j--;

            }


        }

        //}

        return graph;
    }


}

