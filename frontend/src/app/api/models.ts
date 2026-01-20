export interface User {
  id: number;
  name: string;
  email: string;
  major: string | null;
  availability: string | null;
  role: 'STUDENT' | 'ADMIN';
}

export interface ChecklistItem {
  id: number;
  title: string;
  description: string | null;
  sortOrder: number;
  language: string; 
}
