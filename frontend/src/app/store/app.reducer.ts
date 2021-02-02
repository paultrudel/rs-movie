import { ActionReducerMap } from '@ngrx/store';

import * as fromAuth from '../auth/store/auth.reducer';
import * as fromMovies from '../movies/store/movies.reducer';
import * as fromHome from '../home/store/home.reducer';
import * as fromMovie from '../movies/movie-detail/store/movie.reducer';

export interface AppState {
    auth: fromAuth.State;
    movies: fromMovies.State;
    home: fromHome.State;
    movie: fromMovie.State;
}

export const appReducer: ActionReducerMap<AppState> = {
    auth: fromAuth.authReducer,
    movies: fromMovies.moviesReducer,
    home: fromHome.homeReducer,
    movie: fromMovie.movieReducer
};