import { Component, ViewChild, ElementRef, OnInit } from '@angular/core';
import { Movie } from './models/movie.model';
import {NgIf} from '@angular/common';
import {MovieResultsComponent} from './components/movie-results/movie-results.component';
import {MovieSearchComponent} from './components/movie-search/movie-search.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  imports: [
    NgIf,
    MovieResultsComponent,
    MovieSearchComponent
  ],
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'Movie Mind - AI-Powered Movie Recommendations';
  movies: Movie[] = [];
  isLoading = false;
  hasError = false;
  errorMessage = '';

  
  private readonly STORAGE_KEY = 'movie-recommender-results';

  @ViewChild('resultsSection') resultsSection: ElementRef | undefined;

  ngOnInit(): void {
    
    this.loadSavedResults();
  }

  onMoviesReceived(movies: Movie[]): void {
    this.movies = movies;

    
    this.saveResults(movies);

    
    setTimeout(() => {
      this.scrollToResults();
    }, 100);
  }

  onLoadingChange(isLoading: boolean): void {
    this.isLoading = isLoading;

    
    if (!isLoading && this.movies.length > 0) {
      setTimeout(() => {
        this.scrollToResults();
      }, 100);
    }
  }

  onError(error: string): void {
    this.hasError = true;
    this.errorMessage = error;
  }

  private saveResults(movies: Movie[]): void {
    try {
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(movies));
    } catch (error) {
      console.error('Error saving results to localStorage:', error);
    }
  }

  private loadSavedResults(): void {
    try {
      const savedResults = localStorage.getItem(this.STORAGE_KEY);
      if (savedResults) {
        this.movies = JSON.parse(savedResults);
      }
    } catch (error) {
      console.error('Error loading results from localStorage:', error);
      
      localStorage.removeItem(this.STORAGE_KEY);
    }
  }

  clearResults(): void {
    this.movies = [];
    localStorage.removeItem(this.STORAGE_KEY);
  }

  private scrollToResults(): void {
    if (this.resultsSection && this.movies.length > 0) {
      this.resultsSection.nativeElement.scrollIntoView({
        behavior: 'smooth',
        block: 'start'
      });
    }
  }
}
