import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Actions, Effect, ofType } from "@ngrx/effects";
import { Store } from "@ngrx/store";
import { map, switchMap } from 'rxjs/operators';

import { Movie } from "src/app/shared/models/movie.model";
import * as fromApp from '../../store/app.reducer';
import * as MoviesActions from './movies.actions';

interface MovieSearchResponse {
    content: Movie[],
    pageable: {
        sort: {
            sorted: boolean,
            unsorted: boolean,
            empty: boolean,
        },
        offset:number,
        pageSize: number,
        pageNumber: number,
        unpaged: boolean,
        paged: true
    },
    last: boolean,
    totalElements: number,
    totalPages: number,
    size: number,
    number: number,
    sort: {
        sorted: boolean,
        unsorted: boolean,
        empty: boolean,
    },
    numberOfElements: number,
    first: boolean,
    empty: boolean
}

@Injectable()
export class MoviesEffects {

    private resourceUrl = 'http://localhost:8080/api/movies/search';
    private searchQuery: string;

    @Effect()
    searchMovies$ = this.actions$.pipe(
        ofType<MoviesActions.SearchMovies>(MoviesActions.SEARCH_MOVIES),
        switchMap(action => {
            const token = action.payload.authUser.token;
            const query = action.payload.query;
            this.searchQuery = query;
            const page = action.payload.page - 1;
            const pageSize = action.payload.pageSize;
            const url = `${this.resourceUrl}?query=${query}&page=${page}&size=${pageSize}`;
            const headers = new HttpHeaders({'Authorization': 'Bearer '+ token});
            return this.http.get<MovieSearchResponse>(url, { headers: headers });
        }), map(response => {
            return {
                movies: response.content.map(movie => {
                    return {
                        ...movie
                    };
                }),
                pageNumber: response.pageable.pageNumber + 1,
                pageSize: response.pageable.pageSize,
                totalElements: response.totalElements,
                query: this.searchQuery
            }
        }), map(data => {
            return new MoviesActions.SetMovies(data);
        })
    );
    
    constructor(private actions$: Actions, private http: HttpClient, private store: Store<fromApp.AppState>) {}
}