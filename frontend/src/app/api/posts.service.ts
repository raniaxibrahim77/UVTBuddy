import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

export type PostType = 'NOTE' | 'QUESTION' | 'TIP';

export interface StudyPost {
  id: number;
  title: string;
  description: string;
  course?: string | null;
  major?: string | null;
  createdAt: string;
  tags?: string[];
  imageUrls?: string[] | null;
  type: PostType;
  language?: string | null;

  author: { id: number; name: string; email: string };
}

export interface CreatePostReq {
  title: string;
  description: string;
  course?: string | null;
  major?: string | null;
  tags?: string[];
  type: PostType;
  authorId: number;
}

/** Comments */
export type CommentDto = {
  id: number;
  text: string;
  createdAt: string;
  author: { id: number; name: string };
};

@Injectable({ providedIn: 'root' })
export class PostsService {
  private apiUrl = '/api/posts';

  constructor(private http: HttpClient) {}

  all(params?: { q?: string; type?: PostType | 'ALL' }): Promise<StudyPost[]> {
    let httpParams = new HttpParams();

    if (params?.q) httpParams = httpParams.set('q', params.q);
    if (params?.type && params.type !== 'ALL') httpParams = httpParams.set('type', params.type);

    return firstValueFrom(this.http.get<StudyPost[]>(this.apiUrl, { params: httpParams }));
  }

  one(id: number): Promise<StudyPost> {
    return firstValueFrom(this.http.get<StudyPost>(`${this.apiUrl}/${id}`));
  }

  create(req: CreatePostReq): Promise<StudyPost> {
    return firstValueFrom(this.http.post<StudyPost>(this.apiUrl, req));
  }

  delete(id: number): Promise<void> {
    return firstValueFrom(this.http.delete<void>(`${this.apiUrl}/${id}`));
  }

  uploadImages(postId: number, files: File[]): Promise<StudyPost> {
    const form = new FormData();
    for (const f of files) form.append('files', f);

    return firstValueFrom(
      this.http.post<StudyPost>(`${this.apiUrl}/${postId}/images`, form)
    );
  }

  listComments(postId: number): Promise<CommentDto[]> {
    return firstValueFrom(
      this.http.get<CommentDto[]>(`${this.apiUrl}/${postId}/comments`)
    );
  }

  createComment(postId: number, text: string): Promise<CommentDto> {
    return firstValueFrom(
      this.http.post<CommentDto>(`${this.apiUrl}/${postId}/comments`, { text })
    );
  }

  deleteComment(postId: number, commentId: number): Promise<void> {
    return firstValueFrom(
      this.http.delete<void>(`${this.apiUrl}/${postId}/comments/${commentId}`)
    );
  }
}
