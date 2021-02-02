import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';

import * as fromApp from '../store/app.reducer';
import * as MoviesActions from '../movies/store/movies.actions';
import { AuthUser } from '../shared/models/auth-user.model';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit, OnDestroy {

  authUser: AuthUser;
  pageSize: number;
  moviesSub: Subscription;
  authSub: Subscription

  constructor(private store: Store<fromApp.AppState>) { }

  ngOnInit(): void {
    this.authSub = this.store.select('auth').subscribe(authState => {
      this.authUser = authState.authUser;
    });

    this.moviesSub = this.store.select('movies').subscribe(moviesState => {
      this.pageSize = moviesState.pageSize;
    });
  }

  onSearchMovies(query: string) {
    this.store.dispatch(new MoviesActions.SearchMovies({
      authUser: this.authUser,
      query: query,
      page: 1,
      pageSize: this.pageSize
    }));
  }

  ngOnDestroy() {
    if(this.moviesSub) {
      this.moviesSub.unsubscribe();
    }
    if(this.authSub) {
      this.authSub.unsubscribe();
    }
  }

}
