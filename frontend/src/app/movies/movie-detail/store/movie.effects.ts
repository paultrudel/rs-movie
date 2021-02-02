import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Actions, Effect, ofType } from "@ngrx/effects";
import { Store } from "@ngrx/store";
import { map, switchMap } from "rxjs/operators";
import { AuthUser } from "src/app/shared/models/auth-user.model";
import { Movie } from "src/app/shared/models/movie.model";
import { Review } from "src/app/shared/models/review.model";

import * as fromApp from '../../../store/app.reducer';
import * as MovieActions from './movie.actions';

interface ReviewsResponse {
    content: Review[],
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
export class MovieEffects {

    private resourceUrl = 'http://localhost:8080/api';

    @Effect()
    fetchMovie$ = this.actions$.pipe(
        ofType<MovieActions.FetchMovie>(MovieActions.FETCH_MOVIE),
        switchMap(action => {
            const token = action.payload.authUser.token;
            const movieId = action.payload.movieId;
            const url = `${this.resourceUrl}/movies/${movieId}`;
            const headers = new HttpHeaders({'Authorization': 'Bearer '+ token});
            return this.http.get<Movie>(url, { headers: headers });
        }), map(response => {
            return new MovieActions.SetMovie(response);
        })
    );

    @Effect()
    fetchReviews$ = this.actions$.pipe(
        ofType<MovieActions.FetchReviews>(MovieActions.FETCH_REVIEWS),
        switchMap(action => {
            const token = action.payload.authUser.token;
            const movieId = action.payload.movieId;
            const page = action.payload.page - 1;
            const pageSize = action.payload.pageSize;
            const url = `${this.resourceUrl}/reviews/movie/${movieId}?page=${page}&size=${pageSize}`;
            const headers = new HttpHeaders({'Authorization': 'Bearer '+ token});
            return this.http.get<ReviewsResponse>(url, { headers: headers });
        }), map(response => {
            return {
                reviews: response.content.map(review => {
                    return {
                        ...review
                    };
                }),
                pageNumber: response.pageable.pageNumber + 1,
                pageSize: response.pageable.pageSize,
                totalElements: response.totalElements,
            }
        }), map(data => {
            return new MovieActions.SetReviews(data);
        })
    );

    @Effect()
    addReview = this.actions$.pipe(
        ofType<MovieActions.AddReview>(MovieActions.ADD_REVIEW),
        switchMap(action => {
            const token = action.payload.authUser.token;
            const username = action.payload.authUser.username;
            const movieId = action.payload.movieId;
            const score = action.payload.score;
            const review = action.payload.review;
            const headers = new HttpHeaders({'Authorization': 'Bearer '+ token});
            const url = `${this.resourceUrl}/reviews`;
            return this.http.post<ReviewsResponse>(url, {
                username: username,
                movieId: movieId,
                score: score,
                review: review
            }, { headers: headers })
        }), map(response => {
            return {
                reviews: response.content.map(review => {
                    return {
                        ...review
                    };
                }),
                pageNumber: response.pageable.pageNumber + 1,
                pageSize: response.pageable.pageSize,
                totalElements: response.totalElements,
            }
        }), map(data => {
            return new MovieActions.SetReviews(data);
        })
    );

    constructor(private actions$: Actions, private http: HttpClient, private store: Store<fromApp.AppState>) {}
}