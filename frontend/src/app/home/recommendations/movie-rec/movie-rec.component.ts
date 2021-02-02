import { Component, Input, OnInit } from '@angular/core';
import { Movie } from 'src/app/shared/models/movie.model';

@Component({
  selector: 'app-movie-rec',
  templateUrl: './movie-rec.component.html',
  styleUrls: ['./movie-rec.component.scss']
})
export class MovieRecComponent implements OnInit {

  @Input() movies: Movie[];

  constructor() { }

  ngOnInit(): void {
  }

}
