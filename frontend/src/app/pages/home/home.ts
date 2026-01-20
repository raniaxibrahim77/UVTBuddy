import { Component, Inject, PLATFORM_ID, ChangeDetectorRef } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

import { MatToolbarModule } from '@angular/material/toolbar';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { AuthService } from '../../api/auth.service';
import { User } from '../../api/models';
import { UiSyncService } from '../../api/ui-sync.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    MatToolbarModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatChipsModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  email = '';
  password = '';

  user: User | null = null;
  error: string | null = null;
  loading = false;
  name = '';

  constructor(
  private auth: AuthService,
  private router: Router,
  @Inject(PLATFORM_ID) private platformId: Object,
  private cdr: ChangeDetectorRef,
  private uiSync: UiSyncService
) {
    this.user = this.auth.getSavedUser();
}

  private ui(fn: () => void) {
    this.uiSync.run(this.cdr, fn);
  }

  async login() {
  this.ui(() => {
    this.loading = true;
    this.error = null;
    this.user = null;
  });

  try {
    const u = await this.auth.login(this.email, this.password);
    this.ui(() => this.user = u);
  } catch {
    this.ui(() => {
      this.error = 'Login failed. Check email/password.';
      this.auth.logout();
    });
  } finally {
    this.ui(() => this.loading = false);
  }
}


  async signup() {
  
  const name = this.name.trim();
  const email = this.email.trim();
  const password = this.password;

  if (!this.name.trim()) {
  this.ui(() => this.error = 'Name is required.');
  return;
}

  this.ui(() => {
    this.loading = true;
    this.error = null;
  });

  try {
    const u = await this.auth.signupAndLogin(name, email, password);
    this.ui(() => this.user = u);
  } catch (e: any) {
    console.error('Signup error status:', e?.status);
    console.error('Signup error body:', e?.error);

    this.ui(() => {
      this.error =
        e?.status === 409 ? 'Email already exists.' :
        e?.status === 400 ? (e?.error?.message ?? 'Invalid signup data.') :
        'Signup failed.';
      this.user = null;
    });
  } finally {
    this.ui(() => this.loading = false);
  }
}

  logout() {
  this.ui(() => {
    this.auth.logout();
    this.user = null;
    this.error = null;
    this.loading = false;
  });
}
}
