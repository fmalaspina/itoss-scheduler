package com.frsi.itoss.mgr.controllers;

import com.frsi.itoss.ItossManagerApplication;
import com.frsi.itoss.mgr.security.JwtRequest;
import com.frsi.itoss.mgr.security.JwtResponse;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {ItossManagerApplication.class})
@AutoConfigureMockMvc
class AuditControllerTest {

    @Autowired
    private MockMvc mvc;

    String token;

    void setup() throws Exception {
        Gson gson = new Gson();
        var stub = new JwtRequest();
        stub.setUsername("frsi_malaspina.fer");
        stub.setPassword("Nano19682023!");


        String json = gson.toJson(stub, JwtRequest.class);
        var res = mvc.perform(MockMvcRequestBuilders.post("/authenticate")

                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json)).andDo(print()).andExpect(status().isOk()).andReturn();
        var response = res.getResponse();
        var responseEntity = gson.fromJson(response.getContentAsString(),JwtResponse.class);
        token = responseEntity.getToken();
        // Assuming the token is returned in the response body as a JSON field named "token"

        System.out.println(token);
        assertThat(token).isNotNull();
    }


    @Test
    public void testGetRevisionsBetweenDates() throws Exception {
        setup();
        mvc.perform(MockMvcRequestBuilders

                                .get("/audit/revisionsBetweenDates")
                                .accept(MediaType.APPLICATION_JSON)
                        .header("authorization", "Bearer " + token)
                        .param("entity","ct")
                        .param("user", "bcallejas")
                        .param("dateFrom", "2020-01-01T00:00:00")
                        .param("dateTo", "2024-01-01T00:00:00")
                        .param("id","200000")


                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testGetRevisionsDiff() throws Exception {
        setup();
        var rev1 = "2139940";
        var rev2 = "2160744";
        mvc.perform(MockMvcRequestBuilders

                        .get("/audit/revisionsDiff")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("authorization", "Bearer " + token)
                        .param("entity","ct")
                        .param("id","200000")
                        .param("rev1",rev1)
                        .param("rev2",rev2)


                )
                .andDo(print())
                .andExpect(status().isOk());
    }


}
