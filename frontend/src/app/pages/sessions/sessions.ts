import { Component, Inject, PLATFORM_ID, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { SessionsService, StudySession } from '../../api/sessions.service';
import { AuthService } from '../../api/auth.service';
import { User } from '../../api/models';
import { UiSyncService } from '../../api/ui-sync.service';

@Component({
  selector: 'app-sessions',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDividerModule,
    MatProgressBarModule,
    MatSnackBarModule,
  ],
  templateUrl: './sessions.html',
  styleUrl: './sessions.scss',
})
export class Sessions implements OnInit {
  sessions: StudySession[] = [];
  loading = false;
  creating = false;
  error: string | null = null;

  me: User | null = null;
  joiningId: number | null = null;
  leavingId: number | null = null;


  title = '';
  course = '';
  description = '';
  startsAtLocal = ''; 
  durationMinutes = 60;
  capacity: number | null = 6;
  language = '';
  locationText = '';

  constructor(
    private sessionsApi: SessionsService,
    private auth: AuthService,
    @Inject(PLATFORM_ID) private platformId: Object,
    private cdr: ChangeDetectorRef,
    private uiSync: UiSyncService,
    private snack: MatSnackBar
  ) {}

  private ui(fn: () => void) {
    this.uiSync.run(this.cdr, fn);
  }

  private toast(msg: string) {
  this.snack.open(msg, 'OK', { duration: 2500 });
}


  ngOnInit() {
  if (!isPlatformBrowser(this.platformId)) return;

  setTimeout(() => {
    void (async () => {
      const user = await this.auth.restoreUser();
      this.ui(() => {
        this.me = user;
      });
      await this.load();
    })();
  }, 0);
}
  async load() {
    this.ui(() => {
      this.loading = true;
      this.error = null;
    });

    try {
      const res = await this.sessionsApi.all();

      const sorted = [...res].sort(
        (a, b) => new Date(a.startsAt).getTime() - new Date(b.startsAt).getTime()
      );

      this.ui(() => {
        this.sessions = sorted;
      });
    } catch (e) {
      console.error(e);
      this.ui(() => {
        this.error = 'Failed to load sessions.';
        this.sessions = [];
      });
    } finally {
      this.ui(() => {
        this.loading = false;
      });
    }
  }

get canCreate() {
  return !!this.me;
}

get isAdmin() {
  return this.me?.role === 'ADMIN';
}

get isStudent() {
  return this.me?.role === 'STUDENT';
}


  private toLocalDateTimeString(datetimeLocalValue: string): string {
    if (!datetimeLocalValue) return '';
    return datetimeLocalValue.length === 16 ? `${datetimeLocalValue}:00` : datetimeLocalValue;
  }

  async create() {
    if (!this.me) {
      this.ui(() => {
        this.error = 'Please login first (Home page).';
      });
      return;
    }

    if (!this.title.trim() || !this.course.trim() || !this.startsAtLocal) {
      this.ui(() => {
        this.error = 'Title, course and start time are required.';
      });
      return;
    }

    this.ui(() => {
      this.creating = true;
      this.error = null;
    });

    try {
      await this.sessionsApi.create({
        title: this.title.trim(),
        course: this.course.trim(),
        description: this.description.trim() || null,
        startsAt: this.toLocalDateTimeString(this.startsAtLocal),
        durationMinutes: Number(this.durationMinutes),
        capacity: this.capacity ?? null,
        language: this.language.trim() || null,
        locationText: this.locationText.trim() || null,
      });

      this.ui(() => {
        this.title = '';
        this.course = '';
        this.description = '';
        this.startsAtLocal = '';
        this.durationMinutes = 60;
        this.capacity = 6;
        this.language = '';
        this.locationText = '';
      });

      await this.load();
    } catch (e: any) {
      console.error(e);
      this.ui(() => {
        this.error =
          e?.status === 401
            ? 'Unauthorized. Please login again.'
            : 'Failed to create session.';
      });
    } finally {
      this.ui(() => {
        this.creating = false;
      });
    }
  }

  async join(id: number) {
  if (!this.me) { this.ui(() => this.error = 'Please login first (Home page).'); return; }

  this.ui(() => { this.joiningId = id; this.error = null; });

  try {
    const res = await this.sessionsApi.join(id);
    this.ui(() => {
      if (!res.ok) this.error = null;
      else this.error = res.message;
    });
    this.toast(res.message);
    await this.load();
  } catch (e: any) {
    console.error(e);
    const msg = e?.status === 401 ? 'Unauthorized. Please login again.' : 'Failed to join session.';
    this.ui(() => this.error = msg);
    this.toast(msg);
  } finally {
    this.ui(() => this.joiningId = null);
  }
}

async leave(id: number) {
  if (!this.me) { this.ui(() => this.error = 'Please login first (Home page).'); return; }

  this.ui(() => { this.leavingId = id; this.error = null; });

  try {
    const res = await this.sessionsApi.leave(id);
    this.ui(() => {
      if (!res.ok) this.error = null;
      else this.error = res.message;
    });
    this.toast(res.message);
    await this.load();
  } catch (e: any) {
    console.error(e);
    const msg = e?.status === 401 ? 'Unauthorized. Please login again.' : 'Failed to leave session.';
    this.ui(() => this.error = msg);
    this.toast(msg);
  } finally {
    this.ui(() => this.leavingId = null);
  }
}

  async deleteSession(id: number) {
  if (!this.me) {
    this.ui(() => (this.error = 'Please login first (Home page).'));
    return;
  }
  if (!this.isAdmin) {
    this.ui(() => (this.error = 'Only admins can delete sessions.'));
    return;
  }

  this.ui(() => {
    this.error = null;
  });

  try {
    await this.sessionsApi.delete(id);
    await this.load();
  } catch (e: any) {
    console.error(e);
    this.ui(() => {
      this.error =
        e?.status === 403
          ? 'Forbidden: Admin only.'
          : e?.status === 401
          ? 'Unauthorized. Please login again.'
          : 'Failed to delete session.';
    });
  }
}

}
