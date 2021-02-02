import { NgModule } from "@angular/core";

import { RouterModule, Routes } from "@angular/router";
import { SharedModule } from "../shared/shared.module";
import { HomeComponent } from "./home.component";
import { RecommendationsComponent } from './recommendations/recommendations.component';
import { MovieRecComponent } from './recommendations/movie-rec/movie-rec.component';
import { MovieRecItemComponent } from './recommendations/movie-rec/movie-rec-item/movie-rec-item.component';
import { AuthGuard } from '../auth/auth.guard';

const routes: Routes = [
    {path: '', component: HomeComponent, canActivate: [AuthGuard]}
]

@NgModule({
    declarations: [HomeComponent, RecommendationsComponent, MovieRecComponent, MovieRecItemComponent],
    imports: [RouterModule.forChild(routes), SharedModule]
})
export class HomeModule {}