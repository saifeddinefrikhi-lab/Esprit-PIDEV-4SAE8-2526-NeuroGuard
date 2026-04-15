package com.neuroguard.riskalertservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuroguard.riskalertservice.dto.AlertRequest;
import com.neuroguard.riskalertservice.dto.AlertResponse;
import com.neuroguard.riskalertservice.service.AlertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Provider Alert Controller Tests")
class ProviderAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AlertService alertService;

    private AlertRequest request;
    private AlertResponse response;

    @BeforeEach
    void setUp() {
        request = new AlertRequest();
        request.setPatientId(1L);
        request.setMessage("Manual alert");
        request.setSeverity("WARNING");

        response = new AlertResponse();
        response.setId(1L);
        response.setPatientId(1L);
        response.setPatientName("John Doe");
        response.setMessage("Manual alert");
        response.setSeverity("WARNING");
        response.setResolved(false);
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should trigger automatic generation")
    void testTriggerGeneration() throws Exception {
        mockMvc.perform(post("/api/provider/alerts/generate"))
                .andExpect(status().isOk())
                .andExpect(content().string("Alert generation triggered"));

        verify(alertService, times(1)).generateAlertsForAllPatients();
    }

    @Test
    @DisplayName("Should create alert successfully")
    void testCreateAlert_Success() throws Exception {
        when(alertService.createAlert(any(), anyLong())).thenReturn(response);

        mockMvc.perform(post("/api/provider/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .requestAttr("userId", 10L))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.message").value("Manual alert"));

        verify(alertService, times(1)).createAlert(any(), eq(10L));
    }

    @Test
    @DisplayName("Should update alert successfully")
    void testUpdateAlert_Success() throws Exception {
        when(alertService.updateAlert(eq(7L), any(), anyLong())).thenReturn(response);

        mockMvc.perform(put("/api/provider/alerts/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .requestAttr("userId", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.severity").value("WARNING"));

        verify(alertService, times(1)).updateAlert(eq(7L), any(), eq(10L));
    }

    @Test
    @DisplayName("Should delete alert successfully")
    void testDeleteAlert_Success() throws Exception {
        doNothing().when(alertService).deleteAlert(7L, 10L);

        mockMvc.perform(delete("/api/provider/alerts/7")
                        .requestAttr("userId", 10L))
                .andExpect(status().isNoContent());

        verify(alertService, times(1)).deleteAlert(7L, 10L);
    }

    @Test
    @DisplayName("Should resolve alert successfully")
    void testResolveAlert_Success() throws Exception {
        response.setResolved(true);
        when(alertService.resolveAlert(7L, 10L)).thenReturn(response);

        mockMvc.perform(patch("/api/provider/alerts/7/resolve")
                        .requestAttr("userId", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resolved").value(true));

        verify(alertService, times(1)).resolveAlert(7L, 10L);
    }

    @Test
    @DisplayName("Should get alerts by patient")
    void testGetAlertsByPatient_Success() throws Exception {
        when(alertService.getAlertsByPatientId(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/provider/alerts/patient/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(alertService, times(1)).getAlertsByPatientId(1L);
    }

    @Test
    @DisplayName("Should trigger predictive alert generation")
    void testTriggerPredictiveGeneration() throws Exception {
        mockMvc.perform(post("/api/provider/alerts/generate-predictive"))
                .andExpect(status().isOk())
                .andExpect(content().string("Predictive alert generation triggered"));

        verify(alertService, times(1)).generatePredictiveAlertsForAllPatients();
    }

    @Test
    @DisplayName("Should reject invalid alert request")
    void testCreateAlert_InvalidRequest() throws Exception {
        AlertRequest invalidRequest = new AlertRequest();

        mockMvc.perform(post("/api/provider/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .requestAttr("userId", 10L))
                .andExpect(status().isBadRequest());
    }
}
