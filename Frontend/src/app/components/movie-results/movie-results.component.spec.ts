import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MovieResultsComponent } from './movie-results.component';

describe('MovieResultsComponent', () => {
  let component: MovieResultsComponent;
  let fixture: ComponentFixture<MovieResultsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MovieResultsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MovieResultsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
