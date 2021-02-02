import { Movie } from 'src/app/shared/models/movie.model';
import * as MoviesActions from './movies.actions';

export interface State {
    movies: Movie[];
    pageNumber: number;
    pageSize: number;
    totalElements: number;
    lastQuery: string;
    isLoading: boolean;
}

const initialState: State = {
    movies: [],
    pageNumber: 1,
    pageSize: 5,
    totalElements: 0,
    lastQuery: '',
    isLoading: false,
}

export function moviesReducer(state = initialState, action: MoviesActions.MoviesActions) {
    switch(action.type) {
        case MoviesActions.SET_MOVIES:
            return {
                ...state,
                movies: [...action.payload.movies],
                pageNumber: action.payload.pageNumber,
                pageSize: action.payload.pageSize,
                totalElements: action.payload.totalElements,
                lastQuery: action.payload.query,
                isLoading: false
            };
        case MoviesActions.SEARCH_MOVIES:
            return {
                ...state,
                documents: [],
                isLoading: true
            };
        default:
            return state;
    }
}