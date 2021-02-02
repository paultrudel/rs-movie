import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { AuthUser } from 'src/app/shared/models/auth-user.model';
import { Movie } from 'src/app/shared/models/movie.model';

import * as fromApp from '../../store/app.reducer';
import * as MovieActions from './store/movie.actions';

@Component({
  selector: 'app-movie-detail',
  templateUrl: './movie-detail.component.html',
  styleUrls: ['./movie-detail.component.scss']
})
export class MovieDetailComponent implements OnInit, OnDestroy {

  authUser: AuthUser;
  movieId: string;
  movie: Movie;
  isLoading = true;
  authSub: Subscription;

  intScore: number;
  twoDecimalScore: number;

  constructor(private route: ActivatedRoute, private store: Store<fromApp.AppState>) { }

  ngOnInit(): void {
    this.authSub = this.store.select('auth').subscribe(authState => {
      this.authUser = authState.authUser;
    })

    this.route.params.pipe(
      map(params => {
        return params['id']
      }), switchMap(id => {
        this.movieId = id;
        this.store.dispatch(new MovieActions.FetchMovie({authUser: this.authUser, movieId: id}));
        return this.store.select('movie');
      })
    ).subscribe(movieState => {
      this.movie = movieState.movie;
      this.isLoading = movieState.isLoading;
      if(this.movie) {
        this.intScore = Math.round(this.movie.score);
        this.twoDecimalScore = Math.round((this.movie.score + Number.EPSILON) * 100) / 100;
      }
    });
  }

  counter(i: number) {
    return new Array(i);
  }


  ngOnDestroy() {
    if(this.authSub) {
      this.authSub.unsubscribe();
    }
  }

}
