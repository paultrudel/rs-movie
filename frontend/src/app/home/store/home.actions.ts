import { Action } from "@ngrx/store";
import { AuthUser } from "src/app/shared/models/auth-user.model";
import { Movie } from "src/app/shared/models/movie.model";

export const FETCH_RECOMMENDATIONS = '[Home] Fetch Recommendations';
export const SET_RECOMMENDATIONS = '[Home] Set Recommendations';

export class FetchRecommendations implements Action {

    readonly type = FETCH_RECOMMENDATIONS;

    constructor(public payload: AuthUser) {}
}

export class SetRecommendations implements Action {

    readonly type = SET_RECOMMENDATIONS;

    constructor(public payload: {
        communityRecs: Movie[],
        personalRecs: Movie[],
        reviewed: Movie[]
    }) {}
}

export type HomeActions = FetchRecommendations | SetRecommendations;