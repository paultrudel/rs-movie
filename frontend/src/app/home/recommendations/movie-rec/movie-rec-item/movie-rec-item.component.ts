import { Component, Input, OnInit } from '@angular/core';
import { Movie } from 'src/app/shared/models/movie.model';

@Component({
  selector: 'app-movie-rec-item',
  templateUrl: './movie-rec-item.component.html',
  styleUrls: ['./movie-rec-item.component.scss']
})
export class MovieRecItemComponent implements OnInit {

  @Input() movie: Movie;
  intScore: number;
  twoDecimalScore: number;

  constructor() { }

  ngOnInit(): void {
    this.intScore = Math.round(this.movie.score);
    this.twoDecimalScore = Math.round((this.movie.score + Number.EPSILON) * 100) / 100;
  }

  counter(i: number) {
    return new Array(i);
  }

}
