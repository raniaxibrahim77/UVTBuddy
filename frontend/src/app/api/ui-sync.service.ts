import { Injectable, NgZone, ChangeDetectorRef } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class UiSyncService {
  constructor(private zone: NgZone) {}

  run(cdr: ChangeDetectorRef, fn: () => void) {
    this.zone.run(() => {
      fn();
      cdr.detectChanges();
    });
  }
}
