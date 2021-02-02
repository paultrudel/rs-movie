import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';

import { Movie } from 'src/app/shared/models/movie.model';
import * as fromApp from '../../store/app.reducer';

@Component({
  selector: 'app-recommendations',
  templateUrl: './recommendations.component.html',
  styleUrls: ['./recommendations.component.scss']
})
export class RecommendationsComponent implements OnInit, OnDestroy {

  communityRecs: Movie[];
  personalRecs: Movie[];
  reviewed: Movie[];
  isLoading = false;
  homeSub: Subscription;

  constructor(private store: Store<fromApp.AppState>) { }

  ngOnInit(): void {
    this.homeSub = this.store.select('home').subscribe(homeState => {
      this.communityRecs = homeState.communityRecs;
      this.personalRecs = homeState.personalRecs;
      this.reviewed = homeState.reviewed;
      this.isLoading = homeState.isLoading;
    });
  }

  ngOnDestroy() {
    if(this.homeSub) {
      this.homeSub.unsubscribe();
    }
  }

}
