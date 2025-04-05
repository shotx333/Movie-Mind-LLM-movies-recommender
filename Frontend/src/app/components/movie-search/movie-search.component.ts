import { Component, EventEmitter, Output, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MovieService } from '../../services/movie.service';
import { Movie } from '../../models/movie.model';
import { Provider } from '../../models/provider.model';
import { finalize } from 'rxjs/operators';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-movie-search',
  templateUrl: './movie-search.component.html',
  styleUrls: ['./movie-search.component.scss'],
  imports: [CommonModule, ReactiveFormsModule]
})
export class MovieSearchComponent implements OnInit {
  @Output() moviesReceived = new EventEmitter<Movie[]>();
  @Output() loadingChange = new EventEmitter<boolean>();
  @Output() error = new EventEmitter<string>();

  searchForm: FormGroup;
  availableProviders: Provider[] = [];
  providersLoading = true;
  providersError = false;

  exampleDescriptions: string[] = [
    "A psychological thriller where the main character can't tell what's real and what's in their mind",
    "A heartwarming animated film about friendship and adventure",
    "A sci-fi movie with time travel paradoxes and philosophical questions",
    "A coming-of-age story set in a small town in the 1980s with a great soundtrack",
    "A visually stunning foreign film with minimal dialogue but powerful imagery"
  ];

  constructor(
    private fb: FormBuilder,
    private movieService: MovieService
  ) {
    this.searchForm = this.fb.group({
      description: ['', [Validators.required, Validators.minLength(10)]],
      maxResults: [3, [Validators.required, Validators.min(1), Validators.max(10)]],
      provider: ['anthropic', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadProviders();
  }

  loadProviders(): void {
    this.providersLoading = true;
    this.movieService.getAvailableProviders()
      .pipe(
        finalize(() => this.providersLoading = false)
      )
      .subscribe({
        next: (providers : Provider[]) => {
          this.availableProviders = providers;
          
          if (providers.length > 0) {
            this.searchForm.patchValue({
              provider: providers[0].id
            });
          }
        },
        error: (err : any) => {
          console.error('Error fetching providers:', err);
          this.providersError = true;
        }
      });
  }

  onSubmit(): void {
    if (this.searchForm.invalid) {
      return;
    }

    const description = this.searchForm.get('description')?.value;
    const maxResults = this.searchForm.get('maxResults')?.value;
    const provider = this.searchForm.get('provider')?.value;

    this.loadingChange.emit(true);
    this.movieService.getMovieRecommendations(description, maxResults, provider)
      .pipe(
        finalize(() => this.loadingChange.emit(false))
      )
      .subscribe({
        next: (movies: Movie[] | undefined) => {
          this.moviesReceived.emit(movies);
        },
        error: (err: any) => {
          console.error('Error fetching recommendations:', err);
          this.error.emit('Failed to get movie recommendations. Please try again later.');
        }
      });
  }

  useExample(example: string): void {
    this.searchForm.patchValue({
      description: example
    });
  }

  getSelectedProviderName(): string {
    const providerId = this.searchForm.get('provider')?.value;
    const provider = this.availableProviders.find(p => p.id === providerId);
    return provider ? provider.name : 'AI';
  }
}
