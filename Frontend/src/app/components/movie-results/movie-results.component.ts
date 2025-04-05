import { Component, Input } from '@angular/core';
import { Movie } from '../../models/movie.model';
import { MovieCardComponent } from "../movie-card/movie-card.component";
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-movie-results',
  templateUrl: './movie-results.component.html',
  styleUrls: ['./movie-results.component.scss'],
  imports: [MovieCardComponent,CommonModule]
})
export class MovieResultsComponent {
  @Input() movies: Movie[] = [];
}