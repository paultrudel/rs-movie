import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { UserComponent } from './user/user.component';


const routes: Routes = [
  {path: '', loadChildren: () => import('./home/home.module').then(mod => mod.HomeModule)},
  {path: 'movies', loadChildren: () => import('./movies/movies.module').then(mod => mod.MoviesModule)},
  {path: 'auth', loadChildren: () => import('./auth/auth.module').then(mod => mod.AuthModule)},
  {path: ':username', component: UserComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
