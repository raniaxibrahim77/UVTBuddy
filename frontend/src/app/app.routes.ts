import { Routes } from '@angular/router';

import { Home } from './pages/home/home';
import { Checklist } from './pages/checklist/checklist';
import { UvtInfo } from './pages/info/uvt-info';
import { Posts } from './pages/posts/posts';
import { authGuard } from './guards/auth.guards';
import { Sessions } from './pages/sessions/sessions';
import { Events } from './pages/events/events';


export const routes: Routes = [
  { path: '', component: Home },

  { path: 'checklist', component: Checklist, canActivate: [authGuard] },
  { path: 'info', component: UvtInfo, canActivate: [authGuard] },
  { path: 'posts', component: Posts, canActivate: [authGuard] },
  { path: 'events', component: Events, canActivate: [authGuard] },   // ✅ add this
  { path: 'sessions', component: Sessions, canActivate: [authGuard] },

  { path: 'contacts', redirectTo: 'info', pathMatch: 'full' },
  { path: '**', redirectTo: '' },
];

