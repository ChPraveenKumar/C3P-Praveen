import { TestBed, inject } from '@angular/core/testing';

import { IpmanagementserviceService } from './ipmanagementservice.service';

describe('IpmanagementserviceService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [IpmanagementserviceService]
    });
  });

  it('should be created', inject([IpmanagementserviceService], (service: IpmanagementserviceService) => {
    expect(service).toBeTruthy();
  }));
});
