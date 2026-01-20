import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { User } from './models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = '/api';
  private tokenKey = 'basicToken';
  private userKey = 'meUser';

  private token: string | null = null; 

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    if (isPlatformBrowser(this.platformId)) {
      this.token = localStorage.getItem(this.tokenKey); 
    }
  }

  private get isBrowser() {
    return isPlatformBrowser(this.platformId);
  }

  setToken(email: string, password: string) {
    if (!this.isBrowser) return;
    const token = btoa(`${email}:${password}`);
    this.token = token; 
    localStorage.setItem(this.tokenKey, token);
  }

  getToken(): string | null {
    return this.token; 
  }

  clearToken() {
    if (!this.isBrowser) return;
    this.token = null; 
    localStorage.removeItem(this.tokenKey);
  }

  saveUser(user: User) {
    if (!this.isBrowser) return;
    localStorage.setItem(this.userKey, JSON.stringify(user));
  }

  getSavedUser(): User | null {
    if (!this.isBrowser) return null;
    const raw = localStorage.getItem(this.userKey);
    return raw ? (JSON.parse(raw) as User) : null;
  }

  clearUser() {
    if (!this.isBrowser) return;
    localStorage.removeItem(this.userKey);
  }

  me(): Promise<User> {
    return firstValueFrom(this.http.get<User>(`${this.apiUrl}/auth/me`));
  }

  meWithToken(token: string): Promise<User> {
  const headers = new HttpHeaders({
    Authorization: `Basic ${token}`,
  });

  return firstValueFrom(
    this.http.get<User>(`${this.apiUrl}/auth/me`, { headers })
  );
}

  async login(email: string, password: string): Promise<User> {
  this.setToken(email, password);    
  const token = this.getToken()!;
  const user = await this.meWithToken(token);
  this.saveUser(user);
  return user;
}

async restoreUser(): Promise<User | null> {
  const saved = this.getSavedUser();
  if (saved) return saved;

  const token = this.getToken();
  if (!token) return null;

  try {
    const user = await this.meWithToken(token);
    this.saveUser(user);
    return user;
  } catch {
    this.logout();
    return null;
  }
}

isAdmin(user?: User | null): boolean {
  const u = user ?? this.getSavedUser();
  return u?.role === 'ADMIN';
}

  logout() {
    this.clearToken();
    this.clearUser();
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  signup(name: string, email: string, password: string) {
  return firstValueFrom(
    this.http.post('/api/users', {
      name,
      email,
      password,
      role: 'STUDENT',
    })
  );
}


async signupAndLogin(name: string, email: string, password: string): Promise<User> {
  await this.signup(name, email, password);
  return this.login(email, password);
}

}


