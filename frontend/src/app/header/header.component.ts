import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';

import * as fromApp from '../store/app.reducer';
import * as AuthActions from '../auth/store/auth.actions'; 
import { Store } from '@ngrx/store';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit, OnDestroy {

  isAuthenticated = false;
  username: string;
  private authSub: Subscription;

  constructor(private store: Store<fromApp.AppState>) { }

  ngOnInit(): void {
    this.authSub = this.store.select('auth').pipe(
      map(authState => authState.authUser)
    ).subscribe(authUser => {
      this.isAuthenticated = !!authUser;
      if(authUser) {
        this.username = authUser.username;
      }
    });
  }

  onLogout() {
    this.store.dispatch(new AuthActions.Logout());
  }

  ngOnDestroy() {
    this.authSub.unsubscribe();
  }

}
