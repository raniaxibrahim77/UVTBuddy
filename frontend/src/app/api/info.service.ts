import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

export type InfoCategory =
  | 'CONTACT'
  | 'FORMS'
  | 'INTERNATIONAL'
  | 'EVENTS'
  | 'PLATFORMS';

export interface InfoResource {
  id: number;
  title: string;
  content: string;
  category: InfoCategory;
  link?: string | null;
}

@Injectable({ providedIn: 'root' })
export class InfoService {
  private apiUrl = '/api/info';

  constructor(private http: HttpClient) {}

  list(opts?: { category?: InfoCategory; q?: string }): Promise<InfoResource[]> {
    let params = new HttpParams();
    if (opts?.category) params = params.set('category', opts.category);
    if (opts?.q) params = params.set('q', opts.q);

    return firstValueFrom(
      this.http.get<InfoResource[]>(this.apiUrl, { params })
    );
  }
}
