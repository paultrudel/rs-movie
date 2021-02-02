import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MovieRecComponent } from './movie-rec.component';

describe('MovieRecComponent', () => {
  let component: MovieRecComponent;
  let fixture: ComponentFixture<MovieRecComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MovieRecComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MovieRecComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
