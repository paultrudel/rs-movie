import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import { AuthUser } from '../shared/models/auth-user.model';

import * as fromApp from '../store/app.reducer';
import * as HomeActions from './store/home.actions';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit, OnDestroy {

  authUser: AuthUser;
  authSub: Subscription;

  constructor(private store: Store<fromApp.AppState>) { }

  ngOnInit(): void {
    this.authSub = this.store.select('auth').subscribe(authState => {
      this.authUser = authState.authUser
      this.store.dispatch(new HomeActions.FetchRecommendations(this.authUser));
    });
  }

  ngOnDestroy() {
    if(this.authSub) {
      this.authSub.unsubscribe();
    }
  }

}
