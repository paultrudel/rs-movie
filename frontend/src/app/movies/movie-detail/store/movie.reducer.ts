import { Movie } from "src/app/shared/models/movie.model";
import { Review } from "src/app/shared/models/review.model";

import * as MovieActions from './movie.actions';

export interface State {
    movie: Movie;
    reviews: Review[];
    pageNumber: number;
    pageSize: number;
    totalElements: number;
    isLoading: boolean;
}

const initialState: State = {
    movie: null,
    reviews: [],
    pageNumber: 1,
    pageSize: 5,
    totalElements: 0,
    isLoading: false
}

export function movieReducer(state = initialState, action: MovieActions.MovieActions) {
    switch(action.type) {
        case MovieActions.FETCH_MOVIE:
            return {
                ...state,
                movie: null,
                isLoading: true
            };
        case MovieActions.SET_MOVIE:
            return {
                ...state,
                movie: action.payload,
                isLoading: false
            };
        case MovieActions.FETCH_REVIEWS:
            return {
                ...state,
                reviews: [],
            };
        case MovieActions.SET_REVIEWS:
            return {
                ...state,
                reviews: action.payload.reviews,
                pageNumber: action.payload.pageNumber,
                pageSize: action.payload.pageSize,
                totalElements: action.payload.totalElements,
            };
        case MovieActions.ADD_REVIEW:
            return {
                ...state
            };
        default:
            return state;
    }
}

