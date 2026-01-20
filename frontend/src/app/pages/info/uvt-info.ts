import { Component, Inject, PLATFORM_ID, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MatCardModule } from '@angular/material/card';
import { MatTabsModule } from '@angular/material/tabs';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

import { InfoService, InfoCategory, InfoResource } from '../../api/info.service';
import { UiSyncService } from '../../api/ui-sync.service';

@Component({
  selector: 'app-uvt-info',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatTabsModule,
    MatInputModule,
    MatButtonModule,
  ],
  templateUrl: './uvt-info.html',
  styleUrl: './uvt-info.scss',
})
export class UvtInfo implements OnInit {
  items: InfoResource[] = [];
  loading = false;
  error: string | null = null;

  search = '';
  category: InfoCategory | undefined = 'CONTACT';

  categories: { key?: InfoCategory; label: string }[] = [
    { key: 'CONTACT', label: 'Contacts' },
    { key: 'PLATFORMS', label: 'Platforms' },
    { key: 'FORMS', label: 'Requests / Forms / Academic Documents' },
    { key: 'INTERNATIONAL', label: 'International' },
    { key: undefined, label: 'All' },
  ];

  constructor(
    private info: InfoService,
    @Inject(PLATFORM_ID) private platformId: Object,
    private cdr: ChangeDetectorRef,
    private uiSync: UiSyncService
  ) {}

  private ui(fn: () => void) {
    this.uiSync.run(this.cdr, fn);
  }

  ngOnInit() {
    if (!isPlatformBrowser(this.platformId)) return;

    setTimeout(() => {
      void this.load();
    }, 0);
  }

  async load() {
    this.ui(() => {
      this.loading = true;
      this.error = null;
    });

    try {
      const res = await this.info.list({
        category: this.category,
        q: this.search || undefined,
      });

      this.ui(() => {
        this.items = res;
      });
    } catch (e) {
      console.error(e);
      this.ui(() => {
        this.error = 'Failed to load info resources.';
        this.items = [];
      });
    } finally {
      this.ui(() => {
        this.loading = false;
      });
    }
  }

  selectCategory(cat?: InfoCategory) {
    this.ui(() => {
      this.category = cat;
    });
    void this.load();
  }

  private searchTimer?: number;

  searchChanged() {
  if (this.searchTimer) {
    clearTimeout(this.searchTimer);
  }

  this.searchTimer = window.setTimeout(() => {
    void this.load();
  }, 250);
  }
}