export class AuthUser {
     
    constructor(public username: string, private _token: string, private _tokenExpirationDate: Date) {}

    get token() {
        if(!this._tokenExpirationDate || new Date() > this._tokenExpirationDate) {
            return null;
        }
        return this._token;
    }
}