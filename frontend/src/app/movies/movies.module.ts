import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { SharedModule } from "../shared/shared.module";
import { MovieDetailComponent } from "./movie-detail/movie-detail.component";
import { MovieItemComponent } from "./movies-list/movie-item/movie-item.component";
import { MoviesListComponent } from "./movies-list/movies-list.component";
import { MoviesComponent } from "./movies.component";
import { ReviewsComponent } from './movie-detail/reviews/reviews.component';
import { ReviewItemComponent } from './movie-detail/reviews/review-item/review-item.component';
import { FormsModule, ReactiveFormsModule } from "@angular/forms";

const routes: Routes = [
    {path: '', component: MoviesComponent, children: [
        {path: 'list', component: MoviesListComponent},
        {path: ':id', component: MovieDetailComponent}
    ]}
]

@NgModule({
    declarations: [MoviesComponent, MovieDetailComponent, MoviesListComponent, MovieItemComponent, ReviewsComponent, ReviewItemComponent],
    imports: [SharedModule, FormsModule, ReactiveFormsModule, RouterModule.forChild(routes)]
})
export class MoviesModule {}