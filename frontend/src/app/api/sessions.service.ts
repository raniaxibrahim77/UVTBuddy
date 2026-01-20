import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

export type StudySession = {
  id: number;
  title: string;
  course: string;
  description?: string | null;
  startsAt: string;
  durationMinutes: number;
  capacity: number;
  language?: string | null;
  locationText?: string | null;
  creator?: { id: number; name: string; email?: string } | null;

  joinedCount: number;
  joinedByMe: boolean;
};

export type CreateSessionRequest = {
  title: string;
  course: string;
  description?: string | null;
  startsAt: string; 
  durationMinutes: number;
  capacity?: number | null;
  language?: string | null;
  locationText?: string | null;
};

export type JoinResult = { ok: boolean; message: string };


@Injectable({ providedIn: 'root' })
export class SessionsService {
  private apiUrl = '/api/sessions';

  constructor(private http: HttpClient) {}

  all(): Promise<StudySession[]> {
    return firstValueFrom(this.http.get<StudySession[]>(this.apiUrl));
  }

  create(req: CreateSessionRequest): Promise<StudySession> {
    return firstValueFrom(this.http.post<StudySession>(this.apiUrl, req));
  }

  join(id: number): Promise<JoinResult> {
  return firstValueFrom(this.http.post<JoinResult>(`${this.apiUrl}/${id}/join`, {}));
}

  leave(id: number): Promise<JoinResult> {
  return firstValueFrom(this.http.post<JoinResult>(`${this.apiUrl}/${id}/leave`, {}));
}

  delete(id: number): Promise<void> {
  return firstValueFrom(this.http.delete<void>(`${this.apiUrl}/${id}`));
}
}
