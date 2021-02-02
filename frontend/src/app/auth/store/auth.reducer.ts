import { AuthUser } from "src/app/shared/models/auth-user.model";
import * as AuthActions from "./auth.actions";

export interface State {
    authUser: AuthUser
    authError: string;
    loading: boolean;
}

const initialState: State = {
    authUser: null,
    authError: null,
    loading: false
};

export function authReducer(state = initialState, action: AuthActions.AuthActions) {
    switch(action.type) {
        case AuthActions.AUTHENTICATE_SUCCESS:
            const authUser = new AuthUser(
                action.payload.username,
                action.payload.token,
                action.payload.expirationDate
            );
            return {
                ...state,
                authUser: authUser,
                authError: null,
                loading: false
            };
        case AuthActions.LOGOUT:
            return {
                ...state,
                user: null
            };
        case AuthActions.LOGIN_START:
        case AuthActions.SIGNUP_START:
            return {
                ...state,
                authError: null,
                loading: true
            };
        case AuthActions.AUTHENTICATE_FAIL:
            return {
                ...state,
                user: null,
                authError: action.payload,
                loading: false
            };
        case AuthActions.CLEAR_ERROR:
            return {
                ...state,
                authError: null
            };
        default:
            return state;
    }
}