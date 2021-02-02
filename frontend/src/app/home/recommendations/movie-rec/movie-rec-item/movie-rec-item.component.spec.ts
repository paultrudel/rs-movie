import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MovieRecItemComponent } from './movie-rec-item.component';

describe('MovieRecItemComponent', () => {
  let component: MovieRecItemComponent;
  let fixture: ComponentFixture<MovieRecItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MovieRecItemComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MovieRecItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
