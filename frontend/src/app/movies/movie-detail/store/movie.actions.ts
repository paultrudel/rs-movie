import { Action } from "@ngrx/store";
import { AuthUser } from "src/app/shared/models/auth-user.model";
import { Movie } from "src/app/shared/models/movie.model";
import { Review } from "src/app/shared/models/review.model";

export const FETCH_MOVIE = '[Movie] Fetch Movie';
export const SET_MOVIE = '[Movie] Set Movie';
export const FETCH_REVIEWS = '[Movie] Fetch Reviews';
export const SET_REVIEWS = '[Movie] Set Reviews';
export const ADD_REVIEW = '[Movie] Add Review';

export class FetchMovie implements Action {

    readonly type = FETCH_MOVIE;

    constructor(public payload: {
        authUser: AuthUser,
        movieId: string
    }) {}
}

export class SetMovie implements Action {

    readonly type = SET_MOVIE;

    constructor(public payload: Movie) {}
}

export class FetchReviews implements Action {

    readonly type = FETCH_REVIEWS;

    constructor(public payload: {
        authUser: AuthUser,
        movieId: string,
        page: number,
        pageSize: number
    }) {}
}

export class SetReviews implements Action {

    readonly type = SET_REVIEWS;

    constructor(public payload: {
        reviews: Review[],
        pageNumber: number,
        pageSize: number,
        totalElements: number,
    }) {}
}

export class AddReview implements Action {

    readonly type = ADD_REVIEW;

    constructor(public payload: {
        authUser: AuthUser,
        movieId: string,
        score: number,
        review: string
    }) {}
}

export type MovieActions = FetchMovie | SetMovie | FetchReviews | SetReviews | AddReview;