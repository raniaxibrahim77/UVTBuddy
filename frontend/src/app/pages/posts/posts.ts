import { Component, Inject, PLATFORM_ID, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatSelectModule } from '@angular/material/select';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

import { PostsService, StudyPost } from '../../api/posts.service';
import { AuthService } from '../../api/auth.service';
import { User } from '../../api/models';
import { UiSyncService } from '../../api/ui-sync.service';
import { PostDialog } from './post-dialog';

@Component({
  selector: 'app-posts',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatChipsModule,
    MatDividerModule,
    MatSelectModule,
    MatDialogModule,

  ],
  templateUrl: './posts.html',
  styleUrls: ['./posts.scss'],
})
export class Posts implements OnInit {
  posts: StudyPost[] = [];
  loading = false;
  creating = false;
  error: string | null = null;

  // create form
  type: 'NOTE' | 'QUESTION' | 'TIP' = 'QUESTION';
  title = '';
  description = '';
  course = '';
  major = '';
  tagsText = '';
  selectedFiles: File[] = [];

  // auth
  me: User | null = null;

  // filters (server-side)
  search = '';
  typeFilter: 'ALL' | 'NOTE' | 'QUESTION' | 'TIP' = 'ALL';

  private searchTimer?: number;

  constructor(
    private postsApi: PostsService,
    private auth: AuthService,
    @Inject(PLATFORM_ID) private platformId: Object,
    private cdr: ChangeDetectorRef,
    private uiSync: UiSyncService,
    private dialog: MatDialog

  ) {}

  private ui(fn: () => void) {
    this.uiSync.run(this.cdr, fn);
  }

  ngOnInit() {
    if (!isPlatformBrowser(this.platformId)) return;

    setTimeout(() => {
      this.ui(() => {
        this.me = this.auth.getSavedUser();
      });
      void this.load();
    }, 0);
  }

  searchChanged() {
    if (this.searchTimer) clearTimeout(this.searchTimer);
    this.searchTimer = window.setTimeout(() => void this.load(), 250);
  }

  async load() {
    this.ui(() => {
      this.loading = true;
      this.error = null;
    });

    try {
      const res = await this.postsApi.all({
        q: this.search || undefined,
        type: this.typeFilter === 'ALL' ? undefined : this.typeFilter,
      });

      const sorted = [...res].sort(
        (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
      );

      this.ui(() => {
        this.posts = sorted;
      });
    } catch (e) {
      console.error(e);
      this.ui(() => {
        this.error = 'Failed to load posts.';
        this.posts = [];
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

  onFilesSelected(ev: Event) {
    const input = ev.target as HTMLInputElement;
    this.selectedFiles = input.files ? Array.from(input.files) : [];
  }

  async create() {
    if (!this.me) {
      this.ui(() => (this.error = 'Please login first (Home page).'));
      return;
    }

    if (!this.title.trim() || !this.description.trim()) {
      this.ui(() => (this.error = 'Title and description are required.'));
      return;
    }

    const tags = this.tagsText
      .split(',')
      .map(t => t.trim())
      .filter(Boolean);

    this.ui(() => {
      this.creating = true;
      this.error = null;
    });

    try {
      const created = await this.postsApi.create({
        title: this.title.trim(),
        description: this.description.trim(),
        course: this.course.trim() || null,
        major: this.major.trim() || null,
        tags: tags.length ? tags : undefined,
        type: this.type,
        authorId: this.me.id,
      });

      if (this.selectedFiles.length) {
        await this.postsApi.uploadImages(created.id, this.selectedFiles);
      }

      this.ui(() => {
        this.title = '';
        this.description = '';
        this.course = '';
        this.major = '';
        this.tagsText = '';
        this.type = 'QUESTION';
        this.selectedFiles = [];
      });

      await this.load();
    } catch (e: any) {
      console.error(e);
      this.ui(() => {
        this.error =
          e?.status === 401
            ? 'Unauthorized. Please login again.'
            : 'Failed to create post.';
      });
    } finally {
      this.ui(() => {
        this.creating = false;
      });
    }
  }

  async deletePost(id: number) {
    if (!this.me) {
      this.ui(() => (this.error = 'Please login first.'));
      return;
    }

    if (!confirm('Are you sure you want to delete this post?')) return;

    this.ui(() => (this.error = null));

    try {
      await this.postsApi.delete(id);
      this.ui(() => {
        this.posts = this.posts.filter(p => p.id !== id);
      });
    } catch (e: any) {
      console.error(e);
      this.ui(() => {
        if (e?.status === 403) this.error = 'Only admins or the author can delete this post.';
        else if (e?.status === 401) this.error = 'Unauthorized. Please login again.';
        else this.error = 'Failed to delete post.';
      });
    }
  }

  openPost(p: StudyPost) {
  this.dialog.open(PostDialog, {
    data: { post: p, meId: this.me?.id, isAdmin: this.isAdmin },
    width: 'min(900px, 92vw)',
    maxWidth: '92vw',
    height: '85vh',
    maxHeight: '85vh',
  });
}


}
