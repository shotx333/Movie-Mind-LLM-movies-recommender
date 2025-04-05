import { Component, Input } from '@angular/core';
import { Movie } from '../../models/movie.model';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-movie-card',
  templateUrl: './movie-card.component.html',
  imports: [
    NgIf
  ],
  styleUrls: ['./movie-card.component.scss']
})
export class MovieCardComponent {
  @Input() movie!: Movie;
  isExpanded = false;

  getMatchScoreColor(): string {
    const score = this.movie.matchScore;
    if (score >= 0.8) return '#4caf50'; 
    if (score >= 0.6) return '#8bc34a'; 
    if (score >= 0.4) return '#ffc107'; 
    if (score >= 0.2) return '#ff9800'; 
    return '#f44336'; 
  }

  getMatchScoreLabel(): string {
    const score = this.movie.matchScore;
    if (score >= 0.8) return 'Excellent Match';
    if (score >= 0.6) return 'Good Match';
    if (score >= 0.4) return 'Fair Match';
    if (score >= 0.2) return 'Weak Match';
    return 'Poor Match';
  }
}
