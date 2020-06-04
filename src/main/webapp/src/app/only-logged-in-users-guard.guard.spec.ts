import { TestBed, async, inject } from '@angular/core/testing';

import { OnlyLoggedInUsersGuardGuard } from './only-logged-in-users-guard.guard';

describe('OnlyLoggedInUsersGuardGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [OnlyLoggedInUsersGuardGuard]
    });
  });

  it('should ...', inject([OnlyLoggedInUsersGuardGuard], (guard: OnlyLoggedInUsersGuardGuard) => {
    expect(guard).toBeTruthy();
  }));
});
