import { HttpClient } from '@angular/common/http';
import { Provider } from '../models/provider.model';
import { Injectable } from '@angular/core';
import { Movie } from '../models/movie.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MovieService {
  private apiUrl = 'http://localhost:8080/api/movies';

  constructor(private http: HttpClient) { }

  getMovieRecommendations(description: string, maxResults: number = 5, provider: string = 'anthropic'): Observable<Movie[]> {
    return this.http.post<Movie[]>(`${this.apiUrl}/recommend`, {
      description,
      maxResults,
      provider
    });
  }
  
  getAvailableProviders(): Observable<Provider[]> {
    return this.http.get<Provider[]>(`${this.apiUrl}/providers`);
  }
}