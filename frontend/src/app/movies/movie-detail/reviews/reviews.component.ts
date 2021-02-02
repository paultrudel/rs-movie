import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import { AuthUser } from 'src/app/shared/models/auth-user.model';

import { Review } from 'src/app/shared/models/review.model';
import * as fromApp from '../../../store/app.reducer';
import * as MovieActions from '../store/movie.actions';

@Component({
  selector: 'app-reviews',
  templateUrl: './reviews.component.html',
  styleUrls: ['./reviews.component.scss']
})
export class ReviewsComponent implements OnInit, OnDestroy {

  @Input() movieId;

  authUser: AuthUser;
  reviews: Review[];
  pageNumber = 1;
  pageSize = 5;
  totalElements: number;
  isLoading = true;
  movieSub: Subscription;
  authSub: Subscription;

  reviewForm = new FormGroup({
    score: new FormControl('', Validators.required),
    content: new FormControl('', Validators.required)
  });

  constructor(private store: Store<fromApp.AppState>) { }

  ngOnInit(): void {
    this.authSub = this.store.select('auth').subscribe(authState => {
      this.authUser = authState.authUser;
      this.store.dispatch(new MovieActions.FetchReviews({
        authUser: this.authUser,
        movieId: this.movieId,
        page: this.pageNumber,
        pageSize: this.pageSize
      })
    );
    })

    this.movieSub = this.store.select('movie').subscribe(movieState => {
      this.reviews = movieState.reviews;
      this.pageNumber = movieState.pageNumber;
      this.pageSize = movieState.pageSize;
      this.totalElements = movieState.totalElements;
    })
  }

  onPageChange() {
    this.store.dispatch(new MovieActions.FetchReviews({
      authUser: this.authUser,
      movieId: this.movieId,
      page: this.pageNumber,
      pageSize: this.pageSize
    }));
  }

  updatePageSize(pageSize: number) {
    this.pageSize = pageSize;
    this.pageNumber = 1;
    this.onPageChange();
  }

  onSubmit() {
    if(!this.reviewForm.valid) {
      return;
    }

    const score = this.reviewForm.value.score;
    const content = this.reviewForm.value.content;

    this.reviewForm.reset();

    this.store.dispatch(new MovieActions.AddReview({
      authUser: this.authUser,
      movieId: this.reviews[0].movie.id,
      score: score,
      review: content
    }));
  }

  ngOnDestroy() {
    if(this.authSub) {
      this.authSub.unsubscribe();
    }
    if(this.movieSub) {
      this.movieSub.unsubscribe();
    }
  }

}
