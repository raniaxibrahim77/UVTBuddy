import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';

import { EventsService, EventItem, EventRequest } from '../../api/events.service';
import { AuthService } from '../../api/auth.service';

@Component({
  selector: 'app-events',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDividerModule,
  ],
  templateUrl: './events.html',
})
export class Events implements OnInit {
  loading = false;
  error = '';

  events: EventItem[] = [];

  // admin form
  form: EventRequest = {
    title: '',
    description: '',
    startsAt: '',
    endsAt: '',
    location: '',
  };

  editingId: number | null = null;

  constructor(private api: EventsService, private auth: AuthService) {}

  ngOnInit(): void {
    this.load();
  }

  get isAdmin(): boolean {
  return this.auth.isAdmin();
}


  load(): void {
    this.loading = true;
    this.error = '';
    this.api.list().subscribe({
      next: (res) => {
        this.events = res ?? [];
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message || 'Failed to load events';
        this.loading = false;
      },
    });
  }

  startEdit(e: EventItem): void {
    this.editingId = e.id;
    this.form = {
      title: e.title ?? '',
      description: e.description ?? '',
      startsAt: toDatetimeLocal(e.startsAt),
      endsAt: e.endsAt ? toDatetimeLocal(e.endsAt) : '',
      location: e.location ?? '',
    };
  }

  cancelEdit(): void {
    this.editingId = null;
    this.resetForm();
  }

  save(): void {
    this.error = '';

    if (!this.form.title?.trim()) {
      this.error = 'Title is required';
      return;
    }
    if (!this.form.startsAt) {
      this.error = 'Start date/time is required';
      return;
    }

    const payload: EventRequest = {
      title: this.form.title.trim(),
      description: this.form.description?.trim() || null,
      startsAt: fromDatetimeLocal(this.form.startsAt),
      endsAt: this.form.endsAt ? fromDatetimeLocal(this.form.endsAt) : null,
      location: this.form.location?.trim() || null,
    };

    this.loading = true;

    if (this.editingId) {
      this.api.update(this.editingId, payload).subscribe({
        next: () => {
          this.loading = false;
          this.cancelEdit();
          this.load();
        },
        error: (err) => {
          this.error = err?.error?.message || 'Failed to update event';
          this.loading = false;
        },
      });
    } else {
      this.api.create(payload).subscribe({
        next: () => {
          this.loading = false;
          this.resetForm();
          this.load();
        },
        error: (err) => {
          this.error = err?.error?.message || 'Failed to create event';
          this.loading = false;
        },
      });
    }
  }

  remove(id: number): void {
    this.error = '';
    this.loading = true;

    this.api.delete(id).subscribe({
      next: () => {
        this.loading = false;
        this.load();
      },
      error: (err) => {
        this.error = err?.error?.message || 'Failed to delete event';
        this.loading = false;
      },
    });
  }

  resetForm(): void {
    this.form = { title: '', description: '', startsAt: '', endsAt: '', location: '' };
  }

  fmt(iso: string): string {
    try {
      return new Date(iso).toLocaleString();
    } catch {
      return iso;
    }
  }
}

function toDatetimeLocal(iso: string): string {
  if (!iso) return '';
  return iso.length >= 16 ? iso.slice(0, 16) : iso;
}

function fromDatetimeLocal(v: string): string {
  if (!v) return v;
  return v.length === 16 ? `${v}:00` : v;
}
