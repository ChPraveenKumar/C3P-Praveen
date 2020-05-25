import { TestBed, inject } from '@angular/core/testing';

import { DashBoardService } from './dash-board.service';

describe('DashBoardService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DashBoardService]
    });
  });

  it('should be created', inject([DashBoardService], (service: DashBoardService) => {
    expect(service).toBeTruthy();
  }));
});
