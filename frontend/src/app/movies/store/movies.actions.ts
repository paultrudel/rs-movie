import { Action } from "@ngrx/store";
import { AuthUser } from "src/app/shared/models/auth-user.model";
import { Movie } from "src/app/shared/models/movie.model";

export const SET_MOVIES = '[Movies] Set Movies';
export const SEARCH_MOVIES = '[Movies] Search Movies';

export class SetMovies implements Action {

    readonly type = SET_MOVIES;

    constructor(public payload: {
        movies: Movie[],
        pageNumber: number,
        pageSize: number,
        totalElements: number,
        query: string
    }) {}
}

export class SearchMovies implements Action {

    readonly type = SEARCH_MOVIES;

    constructor(public payload: {
        authUser: AuthUser,
        query: string,
        page: number,
        pageSize: number
    }) {}
}

export type MoviesActions = SetMovies | SearchMovies;