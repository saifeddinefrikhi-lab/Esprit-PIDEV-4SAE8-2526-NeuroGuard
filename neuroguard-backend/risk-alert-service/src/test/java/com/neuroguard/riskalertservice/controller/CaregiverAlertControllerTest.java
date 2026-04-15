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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("Caregiver Alert Controller Tests")
class CaregiverAlertControllerTest {

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
        response.setMessage("Caregiver alert");
        response.setSeverity("WARNING");
        response.setResolved(false);
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should get assigned patients alerts successfully")
    void testGetAssignedPatientsAlerts_Success() throws Exception {
        when(alertService.getAlertsForCaregiverPatients(2L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/caregiver/alerts")
                        .requestAttr("userId", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(alertService, times(1)).getAlertsForCaregiverPatients(2L);
    }

    @Test
        @DisplayName("Should propagate caregiver service failure")
        void testGetAssignedPatientsAlerts_ServiceFailure() {
        when(alertService.getAlertsForCaregiverPatients(2L))
                .thenThrow(new RuntimeException("Access denied"));

        assertThrows(Exception.class, () ->
            mockMvc.perform(get("/api/caregiver/alerts")
                .requestAttr("userId", 2L)));
    }

    @Test
    @DisplayName("Should resolve alert for caregiver successfully")
    void testResolveAlert_Success() throws Exception {
        response.setResolved(true);
        when(alertService.resolveAlertForCaregiver(1L, 2L)).thenReturn(response);

        mockMvc.perform(patch("/api/caregiver/alerts/1/resolve")
                        .requestAttr("userId", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resolved").value(true));

        verify(alertService, times(1)).resolveAlertForCaregiver(1L, 2L);
    }
}
