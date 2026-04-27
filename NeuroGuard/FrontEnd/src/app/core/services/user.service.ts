import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class UserService {
    private apiUrl = environment.usersApi;

    constructor(private http: HttpClient) { }

    /**
     * Get all patients assigned to a specific caregiver
     */
    getCaregiverPatients(caregiverId: string): Observable<User[]> {
        return this.http.get<User[]>(`${this.apiUrl}/caregiver/${caregiverId}/patients`);
    }
}
