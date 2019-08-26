import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PlannedTransactionsComponent } from './planned-transactions.component';

describe('PlannedTransactionsComponent', () => {
  let component: PlannedTransactionsComponent;
  let fixture: ComponentFixture<PlannedTransactionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PlannedTransactionsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PlannedTransactionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
