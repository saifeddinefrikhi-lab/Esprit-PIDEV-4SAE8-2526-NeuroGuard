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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Patient Alert Controller Tests")
class PatientAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlertService alertService;

    private AlertResponse response;

    @BeforeEach
    void setUp() {
        response = new AlertResponse();
        response.setId(1L);
        response.setPatientId(1L);
        response.setPatientName("John Doe");
        response.setMessage("Patient alert");
        response.setSeverity("INFO");
        response.setResolved(false);
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should get own alerts successfully")
    void testGetMyAlerts_Success() throws Exception {
        when(alertService.getAlertsForPatient(1L, 1L, "PATIENT")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/patient/alerts")
                        .requestAttr("userId", 1L)
                        .requestAttr("userRole", "PATIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(alertService, times(1)).getAlertsForPatient(1L, 1L, "PATIENT");
    }

    @Test
    @DisplayName("Should deny access to other patient's alerts")
    void testGetMyAlerts_AccessDenied() throws Exception {
        when(alertService.getAlertsForPatient(1L, 2L, "PATIENT"))
                .thenThrow(new RuntimeException("Access denied: You can only view your own alerts"));

        mockMvc.perform(get("/api/patient/alerts")
                        .requestAttr("userId", 2L)
                        .requestAttr("userRole", "PATIENT"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should resolve own alert successfully")
    void testResolveAlert_Success() throws Exception {
        response.setResolved(true);
        when(alertService.resolveAlertForPatient(1L, 1L)).thenReturn(response);

        mockMvc.perform(patch("/api/patient/alerts/1/resolve")
                        .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resolved").value(true));

        verify(alertService, times(1)).resolveAlertForPatient(1L, 1L);
    }
}
