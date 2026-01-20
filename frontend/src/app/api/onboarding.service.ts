import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { ChecklistItem } from './models';

@Injectable({ providedIn: 'root' })
export class OnboardingService {
  private apiUrl = '/api';

  constructor(private http: HttpClient) {}

  getChecklist(language: string) {
    return firstValueFrom(
      this.http.get<ChecklistItem[]>(`${this.apiUrl}/onboarding/checklist`, {
        params: { language },
      })
    );
  }

  getProgress(userId: number, language: string) {
  return firstValueFrom(
    this.http.get<number[]>(`/api/onboarding/progress`, {
      params: { userId: String(userId), language },
    })
  );
}

  setDone(itemId: number, userId: number, done: boolean) {
    return firstValueFrom(
      this.http.post(
        `${this.apiUrl}/onboarding/checklist/${itemId}/done`,
        null,
        {
          params: { userId: String(userId), done: String(done) },
          responseType: 'text',
        }
      )
    );
  }
}
