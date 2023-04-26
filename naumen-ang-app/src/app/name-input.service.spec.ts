import { TestBed } from '@angular/core/testing';

import { NameInputService } from './name-input.service';

describe('NameInputService', () => {
  let service: NameInputService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NameInputService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
