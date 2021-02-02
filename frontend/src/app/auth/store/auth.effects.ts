import { Actions, ofType, Effect } from '@ngrx/effects';
import { switchMap, catchError, map, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { of } from 'rxjs';
import { Router } from '@angular/router';

import * as AuthActions from './auth.actions';
import { AuthService } from '../auth.service';
import { AuthUser } from 'src/app/shared/models/auth-user.model';

export interface AuthResponseData {
    access_token: string;
    expires_in: number;
    id_token: string;
    "not-before-policy": number;
    refresh_expires_in: number;
    refresh_token: string;
    scope: string;
    session_state: string;
    token_type: string;
}


const handleAuthentication = (username: string, expiresIn: number, token: string) => {
    const expirationDate = new Date(new Date().getTime() + expiresIn * 1000);
    const authUser = new AuthUser(username, token, expirationDate);
    localStorage.setItem('authUserData', JSON.stringify(authUser));
    return new AuthActions.AuthenticateSuccess({
        username: username,
        token: token,
        expirationDate: expirationDate,
        redirect: true
    });
};

const handleError = (errorRes: any) => {
    let errorMessage = "An unknown error occurred!";
    if(!errorRes.error || !errorRes.error.error) {
        return of(new AuthActions.AuthenticateFail(errorMessage));
    }

    if(errorRes.error && errorRes.error_description) {
        errorMessage = `${errorRes.error}: ${errorRes.error_description}`;
    }
    return of(new AuthActions.AuthenticateFail(errorMessage));
}

@Injectable()
export class AuthEffects {

    authUrl = 'http://localhost:8081/auth/realms/rs-movie/protocol/openid-connect/token';
    resourceUrl = 'http://localhost:8080/api';

    @Effect()
    authSignup = this.actions$.pipe(
        ofType(AuthActions.SIGNUP_START),
        switchMap((signupAction: AuthActions.SignupStart) => {
            return this.http.post<AuthResponseData>(
                `${this.resourceUrl}/users`,
                {
                    username: signupAction.payload.username,
                    password: signupAction.payload.password,
                }
            ).pipe(
                tap(resData => {
                    this.authService.setLogoutTimer(+resData.expires_in * 1000);
                }),
                map(resData => {
                    return handleAuthentication(
                        signupAction.payload.username,
                        +resData.expires_in, 
                        resData.access_token,
                    );
                }),
                catchError(errorRes => {
                    return handleError(errorRes);
                })
            );
        })
    );

    @Effect()
    authLogin = this.actions$.pipe(
        ofType(AuthActions.LOGIN_START),
        switchMap((authData: AuthActions.LoginStart) => {

            let params = new HttpParams()
                .append('client_id', 'newClient')
                .append('grant_type', 'password')
                .append('client_secret', 'newClientSecret')
                .append('scope', 'openid')
                .append('username', authData.payload.username)
                .append('password', authData.payload.password);
            
            let headers = new HttpHeaders({'Content-Type': 'application/x-www-form-urlencoded; charset=utf-8'});

            return this.http.post<AuthResponseData>(
                `${this.authUrl}`,
                params.toString(),
                { headers: headers }
            ).pipe(
                tap(resData => {
                    this.authService.setLogoutTimer(+resData.expires_in * 1000);
                }),
                map(resData => {
                    return handleAuthentication(
                        authData.payload.username,
                        +resData.expires_in, 
                        resData.access_token,
                    );
                }),
                catchError(errorRes => {
                    return handleError(errorRes);
                })
            );
        })
    );

    @Effect({dispatch: false})
    authRedirect = this.actions$.pipe(
        ofType(AuthActions.AUTHENTICATE_SUCCESS), 
        tap((authSuccessAction: AuthActions.AuthenticateSuccess) => {
            if(authSuccessAction.payload.redirect) {
                this.router.navigate(['/']);
            }
        })
    );

    @Effect()
    autoLogin = this.actions$.pipe(
        ofType(AuthActions.AUTO_LOGIN),
        map(() => {
            const authUserData: {
                username: string;
                _token: string;
                _tokenExpirationDate: string;
            } = JSON.parse(localStorage.getItem('authUserData'));
            if (!authUserData) {
                return { type: 'DEFAULT'};
            }

            const loadedAuthUser = new AuthUser(
                authUserData.username,
                authUserData._token,
                new Date(authUserData._tokenExpirationDate)
            );

            if (loadedAuthUser.token) {
                const expirationDuration =
                    new Date(authUserData._tokenExpirationDate).getTime() -
                    new Date().getTime();
                this.authService.setLogoutTimer(expirationDuration);
                return new AuthActions.AuthenticateSuccess({
                    username: loadedAuthUser.username,
                    token: loadedAuthUser.token,
                    expirationDate: new Date(authUserData._tokenExpirationDate),
                    redirect: false
                })
            }
            return { type: 'DEFAULT'};
        })
    );

    @Effect({dispatch: false})
    authLogout = this.actions$.pipe(
        ofType(AuthActions.LOGOUT),
        tap(() => {
            this.authService.clearLogoutTimer();
            localStorage.removeItem('authUserData');
            this.router.navigate(['/auth']);
        })
    );

    constructor(
        private actions$: Actions, 
        private http: HttpClient, 
        private router: Router,
        private authService: AuthService
    ) {}
}