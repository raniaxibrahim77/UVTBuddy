import { Component, Inject, PLATFORM_ID, OnInit } from '@angular/core';
import { isPlatformBrowser, CommonModule } from '@angular/common';
import { ChangeDetectorRef, NgZone } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';


import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';

import { OnboardingService } from '../../api/onboarding.service';
import { AuthService } from '../../api/auth.service';
import { ChecklistItem, User } from '../../api/models';

type UiChecklistItem = ChecklistItem & { done: boolean; saving?: boolean };

@Component({
  selector: 'app-checklist',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    MatCardModule,
    MatCheckboxModule,
    MatProgressBarModule,
    MatSelectModule,
  ],
  templateUrl: './checklist.html',
  styleUrl: './checklist.scss',
})
export class Checklist implements OnInit {
  language: 'en' | 'ro' = 'en';

  items: UiChecklistItem[] = [];
  loading = false;
  error: string | null = null;

  user: User | null = null;
  authLoading = true;

  constructor(
  private onboarding: OnboardingService,
  private auth: AuthService,
  @Inject(PLATFORM_ID) private platformId: Object,
  private cdr: ChangeDetectorRef,
  private zone: NgZone
) {}


  ngOnInit() {
  if (!isPlatformBrowser(this.platformId)) return;

  console.log('Checklist ngOnInit');

  setTimeout(() => {
    void this.init();
  }, 0);
}

private async init() {
  this.ui(() => (this.authLoading = true));

  try {
    const user = await this.auth.restoreUser();
    this.ui(() => (this.user = user));

    await this.load();
  } finally {
    this.ui(() => (this.authLoading = false));
  }
}

  get progressPercent(): number {
    if (this.items.length === 0) return 0;
    const doneCount = this.items.filter(i => i.done).length;
    return Math.round((doneCount / this.items.length) * 100);
  }
  get doneCount(): number {
  return this.items.filter(i => i.done).length;
}

get totalCount(): number {
  return this.items.length;
}

  onLanguageChange(lang: 'en' | 'ro') {
  this.language = lang;
  void this.load();
}

  async load() {
  this.ui(() => {
    this.loading = true;
    this.error = null;
  });

  try {
    const listPromise = this.onboarding.getChecklist(this.language);

    if (!this.user) {
      const list = await listPromise;
      this.ui(() => {
        this.items = list.map(i => ({ ...i, done: false }));
      });
      return;
    }

    const [list, doneIds] = await Promise.all([
      listPromise,
      this.onboarding.getProgress(this.user.id, this.language),
    ]);

    const doneSet = new Set(doneIds);
    this.ui(() => {
      this.items = list.map(i => ({ ...i, done: doneSet.has(i.id) }));
    });
  } catch (e) {
    console.error(e);
    this.ui(() => {
      this.error = 'Failed to load checklist (backend/auth).';
    });
  } finally {
    this.ui(() => {
      this.loading = false;
    });
  }
}

  private ui(fn: () => void) {
  this.zone.run(() => {
    fn();
    this.cdr.detectChanges();
  });
}

  async toggle(item: UiChecklistItem) {
  if (!this.user) {
    this.ui(() => (this.error = 'Please login first (Home page).'));
    return;
  }

  const newValue = !item.done;

  this.ui(() => {
    item.saving = true;
    this.error = null;
    item.done = newValue;
  });

  try {
    await this.onboarding.setDone(item.id, this.user.id, newValue);
  } catch (e) {
    console.error(e);
    this.ui(() => {
      item.done = !newValue;
      this.error = 'Could not save progress. Check backend + auth.';
    });
  } finally {
    this.ui(() => {
      item.saving = false;
    });
  }
}
}
