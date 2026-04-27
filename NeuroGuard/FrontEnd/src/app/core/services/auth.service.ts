import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap, throwError, catchError, map } from 'rxjs';
import { User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
    private apiUrl = 'http://localhost:8081/auth';
    private currentUserSubject = new BehaviorSubject<User | null>(null);
    currentUser$: Observable<User | null> = this.currentUserSubject.asObservable();

    constructor(private http: HttpClient) {
        this.checkAuth();
    }

    get isLoggedIn(): boolean {
        return this.currentUserSubject.value !== null;
    }

    get currentUser(): User | null {
        return this.currentUserSubject.value;
    }

    login(username: string, password: string): Observable<User> {
        return this.http.post<any>(`${this.apiUrl}/login`, { username, password }).pipe(
            map(response => {
                if (response.token) {
                    localStorage.setItem('alzguard_token', response.token);
                    const user = this.decodeToken(response.token);
                    this.currentUserSubject.next(user);
                    localStorage.setItem('alzguard_user', JSON.stringify(user));
                    return user;
                }
                throw new Error('No token found in response');
            }),
            catchError(err => throwError(() => (typeof err === 'string' ? err : err.error?.message || 'Login failed')))
        );
    }

    register(userData: any): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/register`, userData).pipe(
            catchError(err => throwError(() => err.error?.message || 'Registration failed'))
        );
    }

    private decodeToken(token: string): User {
        try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const payload = JSON.parse(atob(base64));

            const userId = payload.userId !== undefined ? payload.userId : (payload.id !== undefined ? payload.id : payload.sub);

            return {
                id: userId ? userId.toString() : 'unknown',
                name: payload.name || payload.username || payload.sub || 'User',
                email: payload.email || (payload.sub && payload.sub.includes('@') ? payload.sub : ''),
                role: (payload.role || payload.roles || 'patient').toString().toLowerCase() as User['role']
            };
        } catch (e) {
            console.error('Error decoding token', e);
            return { id: 'unknown', name: 'User', email: '', role: 'patient' };
        }
    }

    logout(): void {
        this.http.post(`${this.apiUrl}/logout`, {}).subscribe();
        this.currentUserSubject.next(null);
        localStorage.removeItem('alzguard_user');
        localStorage.removeItem('alzguard_token');
    }

    checkAuth(): void {
        const stored = localStorage.getItem('alzguard_user');
        if (stored) {
            this.currentUserSubject.next(JSON.parse(stored));
        }
    }
}
