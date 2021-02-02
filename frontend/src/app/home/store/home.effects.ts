import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Actions, Effect, ofType } from "@ngrx/effects";
import { Store } from "@ngrx/store";
import { map, switchMap } from "rxjs/operators";
import { AuthUser } from "src/app/shared/models/auth-user.model";
import { Movie } from "src/app/shared/models/movie.model";

import * as fromApp from '../../store/app.reducer';
import * as HomeActions from './home.actions';

interface UserRecommendationsResponse {
    community: Movie[],
    personal: Movie[],
    reviewed: Movie[],
}

@Injectable()
export class HomeEffects {

    private resourceUrl = 'http://localhost:8080/api/users/recommendations';

    @Effect()
    fetchRecommendations = this.actions$.pipe(
        ofType<HomeActions.FetchRecommendations>(HomeActions.FETCH_RECOMMENDATIONS),
        switchMap(action => {
            const username = action.payload.username;
            const token = action.payload.token;
            const url = `${this.resourceUrl}/${username}`;
            const headers = new HttpHeaders({'Authorization': 'Bearer '+ token});
            return this.http.get<UserRecommendationsResponse>(url, { headers: headers });
        }), map(response => {
            return {
                communityRecs: response.community.map(movie => {
                    return { ...movie };
                }),
                personalRecs: response.personal.map(movie => {
                    return { ...movie };
                }),
                reviewed: response.reviewed.map(movie => {
                    return { ...movie }
                })
            }
        }), map(data => {
            return new HomeActions.SetRecommendations(data)
        })
    );

    constructor(private actions$: Actions, private http: HttpClient, private store: Store<fromApp.AppState>) {}
}