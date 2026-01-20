import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface EventItem {
  id: number;
  title: string;
  description?: string | null;
  startsAt: string;   
  endsAt?: string | null;
  location?: string | null;
}

export interface EventRequest {
  title: string;
  description?: string | null;
  startsAt: string;
  endsAt?: string | null;
  location?: string | null;
}

@Injectable({ providedIn: 'root' })
export class EventsService {
  private base = '/api/events';

  constructor(private http: HttpClient) {}

  list(): Observable<EventItem[]> {
    return this.http.get<EventItem[]>(this.base);
  }

  create(req: EventRequest): Observable<EventItem> {
    return this.http.post<EventItem>(this.base, req);
  }

  update(id: number, req: Partial<EventRequest>): Observable<EventItem> {
    return this.http.put<EventItem>(`${this.base}/${id}`, req);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
