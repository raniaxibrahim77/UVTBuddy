import { Component, Inject, ChangeDetectorRef, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';

import { PostsService, StudyPost, CommentDto } from '../../api/posts.service';
import { UiSyncService } from '../../api/ui-sync.service';

@Component({
  selector: 'app-post-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatInputModule,
    MatChipsModule,
    MatDividerModule,
  ],
  templateUrl: './post-dialog.html',
  styleUrls: ['./post-dialog.scss'],
})
export class PostDialog implements OnInit {
  post: StudyPost;

  comments: CommentDto[] = [];
  loading = false;
  posting = false;
  error: string | null = null;

  newComment = '';

  private meId?: number;
  private isAdmin = false;

  constructor(
    private postsApi: PostsService,
    private ref: MatDialogRef<PostDialog>,
    @Inject(MAT_DIALOG_DATA) data: { post: StudyPost; meId?: number; isAdmin?: boolean },
    private cdr: ChangeDetectorRef,
    private uiSync: UiSyncService
  ) {
    this.post = data.post;
    this.meId = data.meId;
    this.isAdmin = !!data.isAdmin;
  }

  private ui(fn: () => void) {
    this.uiSync.run(this.cdr, fn);
  }

  ngOnInit(): void {
    setTimeout(() => void this.loadComments(), 0);
  }

  close() {
    this.ref.close();
  }

  async loadComments() {
    this.ui(() => {
      this.loading = true;
      this.error = null;
    });

    try {
      const res = await this.postsApi.listComments(this.post.id);

      this.ui(() => {
        this.comments = res;
      });
    } catch (e) {
      console.error(e);
      this.ui(() => {
        this.error = 'Failed to load comments.';
        this.comments = [];
      });
    } finally {
      this.ui(() => {
        this.loading = false;
      });
    }
  }

  async addComment() {
    const text = this.newComment.trim();
    if (!text) return;

    this.ui(() => {
      this.posting = true;
      this.error = null;
    });

    try {
      const created = await this.postsApi.createComment(this.post.id, text);

       this.ui(() => {
        this.comments = [created, ...this.comments];
        this.newComment = '';
       });

    } catch (e) {
      console.error(e);
      this.ui(() => {
        this.error = 'Failed to post comment.';
      });
    } finally {
      this.ui(() => {
        this.posting = false;
      });
    }
  }

  canDelete(c: CommentDto) {
    return this.isAdmin || (!!this.meId && c.author.id === this.meId);
  }

  async deleteComment(commentId: number) {
    if (!confirm('Delete this comment?')) return;

    this.ui(() => (this.error = null));

    try {
      await this.postsApi.deleteComment(this.post.id, commentId);

      this.ui(() => {
        this.comments = this.comments.filter(c => c.id !== commentId);
      });
    } catch (e) {
      console.error(e);
      this.ui(() => {
        this.error = 'Failed to delete comment.';
      });
    }
  }
}
