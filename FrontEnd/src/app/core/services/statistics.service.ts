import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

/**
 * Patient Statistics DTO
 */
export interface PatientStatisticsDTO {
  patientId: number;
  hasMedicalHistory: boolean;
  progressionStage?: string;
  totalSurgeries: number;
  totalAlerts: number;
  pendingAlerts: number;
  resolvedAlerts: number;
  criticalAlerts: number;
  warningAlerts: number;
  infoAlerts: number;
  comorbiditiesCount: number;
  medicationAllergiesCount: number;
  foodAllergiesCount: number;
  environmentalAllergiesCount: number;
  mmse?: number;
  functionalAssessment?: number;
  adl?: number;
  geneticRisk?: boolean;
  smoking?: boolean;
  cardiovascularDisease?: boolean;
  diabetes?: boolean;
  depression?: boolean;
  totalRiskFactors: number;
  healthConditionCount: number;
}

/**
 * Provider Statistics DTO
 */
export interface ProviderStatisticsDTO {
  providerId: number;
  totalPatients: number;
  totalMedicalHistories: number;
  mildCases: number;
  moderateCases: number;
  severeCases: number;
  totalAlerts: number;
  pendingAlerts: number;
  resolvedAlerts: number;
  criticalAlerts: number;
  warningAlerts: number;
  infoAlerts: number;
  patientsWithGeneticRisk: number;
  patientsWithComorbidities: number;
  patientsWithAllergies: number;
  averageMMSE: number;
  averageFunctionalAssessment: number;
  averageADL: number;
  historyCoverage: number;
  alertCoverageRate: number;
  patientsWithHealthRisks: number;
  averageRiskFactors: number;
}

/**
 * Caregiver Statistics DTO
 */
export interface CaregiverStatisticsDTO {
  caregiverId: number;
  totalAssignedPatients: number;
  patientsWithMedicalHistory: number;
  historyCoverage: number;
  mildCases: number;
  moderateCases: number;
  severeCases: number;
  totalAlerts: number;
  pendingAlerts: number;
  resolvedAlerts: number;
  criticalAlerts: number;
  warningAlerts: number;
  infoAlerts: number;
  averageMMSE: number;
  averageFunctionalAssessment: number;
  averageADL: number;
  alertResolutionRate: number;
  criticalAlertRate: number;
  unresolvedCriticalAlerts: number;
  patientsWithLowMMSE: number;
  patientsWithCognitiveDifficultyOptions: number;
  patientsWithHealthRisks: number;
  averageRiskFactors: number;
}

/**
 * Statistics Service - Fetches aggregated statistics from backend
 * Provides unified access to patient, provider, and caregiver statistics
 */
@Injectable({
  providedIn: 'root'
})
export class StatisticsService {
  private apiUrl = environment.apiUrl + '/api/statistics';
  private http = inject(HttpClient);

  /**
   * Get patient statistics
   */
  getPatientStatistics(patientId: number): Observable<PatientStatisticsDTO> {
    return this.http.get<PatientStatisticsDTO>(`${this.apiUrl}/patient/${patientId}`);
  }

  /**
   * Get current patient's own statistics
   */
  getMyPatientStatistics(): Observable<PatientStatisticsDTO> {
    return this.http.get<PatientStatisticsDTO>(`${this.apiUrl}/patient/me`);
  }

  /**
   * Get provider statistics
   */
  getProviderStatistics(providerId: number): Observable<ProviderStatisticsDTO> {
    return this.http.get<ProviderStatisticsDTO>(`${this.apiUrl}/provider/${providerId}`);
  }

  /**
   * Get current provider's own statistics
   */
  getMyProviderStatistics(): Observable<ProviderStatisticsDTO> {
    return this.http.get<ProviderStatisticsDTO>(`${this.apiUrl}/provider/me`);
  }

  /**
   * Get caregiver statistics
   */
  getCaregiverStatistics(caregiverId: number): Observable<CaregiverStatisticsDTO> {
    return this.http.get<CaregiverStatisticsDTO>(`${this.apiUrl}/caregiver/${caregiverId}`);
  }

  /**
   * Get current caregiver's own statistics
   */
  getMyCaregiverStatistics(): Observable<CaregiverStatisticsDTO> {
    return this.http.get<CaregiverStatisticsDTO>(`${this.apiUrl}/caregiver/me`);
  }
}
