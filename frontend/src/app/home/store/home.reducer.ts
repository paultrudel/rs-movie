import { Movie } from "src/app/shared/models/movie.model";
import * as HomeActions from './home.actions';

export interface State {
    communityRecs: Movie[];
    personalRecs: Movie[];
    reviewed: Movie[];
    isLoading: boolean;
}

const initialState: State = {
    communityRecs: [],
    personalRecs: [],
    reviewed: [],
    isLoading: false
}

export function homeReducer(state = initialState, action: HomeActions.HomeActions) {
    switch(action.type) {
        case HomeActions.FETCH_RECOMMENDATIONS:
            return {
                ...state,
                communityRecs: [],
                personalRecs: [],
                reviewed: [],
                isLoading: true
            };
        case HomeActions.SET_RECOMMENDATIONS:
            return {
                ...state,
                communityRecs: action.payload.communityRecs,
                personalRecs: action.payload.personalRecs,
                reviewed: action.payload.reviewed,
                isLoading: false
            };
        default:
            return state;
    }
}