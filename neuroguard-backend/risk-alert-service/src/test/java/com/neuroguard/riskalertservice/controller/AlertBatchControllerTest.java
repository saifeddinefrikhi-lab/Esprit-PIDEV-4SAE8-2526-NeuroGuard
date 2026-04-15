package com.neuroguard.riskalertservice.controller;

import com.neuroguard.riskalertservice.dto.AlertResponse;
import com.neuroguard.riskalertservice.service.AlertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("Alert Batch Controller Tests")
class AlertBatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlertService alertService;

    private AlertResponse alert1;
    private AlertResponse alert2;

    @BeforeEach
    void setUp() {
        alert1 = new AlertResponse();
        alert1.setId(1L);
        alert1.setPatientId(1L);
        alert1.setPatientName("John Doe");
        alert1.setMessage("Alert 1");
        alert1.setSeverity("INFO");
        alert1.setResolved(false);
        alert1.setCreatedAt(LocalDateTime.now());
        alert1.setUpdatedAt(LocalDateTime.now());

        alert2 = new AlertResponse();
        alert2.setId(2L);
        alert2.setPatientId(2L);
        alert2.setPatientName("Mary Jones");
        alert2.setMessage("Alert 2");
        alert2.setSeverity("CRITICAL");
        alert2.setResolved(false);
        alert2.setCreatedAt(LocalDateTime.now());
        alert2.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should get patient alerts")
    void testGetPatientAlerts_Success() throws Exception {
        when(alertService.getAlertsByPatientId(1L)).thenReturn(List.of(alert1));

        mockMvc.perform(get("/alerts/patient/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(alertService, times(1)).getAlertsByPatientId(1L);
    }

    @Test
    @DisplayName("Should get batch alerts")
    void testGetAlertsBatch_Success() throws Exception {
        when(alertService.getAlertsByPatientId(1L)).thenReturn(List.of(alert1));
        when(alertService.getAlertsByPatientId(2L)).thenReturn(List.of(alert2));

        mockMvc.perform(post("/alerts/batch")
                        .contentType("application/json")
                        .content("[1,2]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Should get unresolved alerts")
    void testGetUnresolvedAlerts_Success() throws Exception {
        when(alertService.getAlertsByPatientId(1L)).thenReturn(List.of(alert1, alert2));

        mockMvc.perform(get("/alerts/patient/1/unresolved"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Should get critical alerts")
    void testGetCriticalAlerts_Success() throws Exception {
        when(alertService.getAlertsByPatientId(1L)).thenReturn(List.of(alert1, alert2));

        mockMvc.perform(get("/alerts/patient/1/critical"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("Should get unresolved critical alerts")
    void testGetUnresolvedCriticalAlerts_Success() throws Exception {
        when(alertService.getAlertsByPatientId(1L)).thenReturn(List.of(alert1, alert2));

        mockMvc.perform(get("/alerts/patient/1/critical/unresolved"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
