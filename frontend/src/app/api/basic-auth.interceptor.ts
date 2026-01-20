import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const basicAuthInterceptor: HttpInterceptorFn = (req, next) => {
  
  if (!req.url.startsWith('/api')) return next(req);

  const auth = inject(AuthService);
  const token = auth.getToken();
  if (!token) return next(req);

  return next(
    req.clone({
      setHeaders: { Authorization: `Basic ${token}` },
    })
  );
};
