import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { StoreRouterConnectingModule } from '@ngrx/router-store';

import { AppRoutingModule } from './app-routing.module';
import { SharedModule } from './shared/shared.module'; 
import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import * as fromApp from './store/app.reducer'
import { AuthEffects } from './auth/store/auth.effects';
import { environment } from '../environments/environment';
import { CoreModule } from './core.module';
import { FormsModule } from '@angular/forms';
import { SearchComponent } from './search/search.component';
import { UserComponent } from './user/user.component';
import { MoviesEffects } from './movies/store/movies.effects';
import { HomeEffects } from './home/store/home.effects';
import { MovieEffects } from './movies/movie-detail/store/movie.effects';

@NgModule({
  declarations: [AppComponent, HeaderComponent, SearchComponent, UserComponent],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    AppRoutingModule,
    StoreModule.forRoot(fromApp.appReducer),
    EffectsModule.forRoot([AuthEffects, MoviesEffects, HomeEffects, MovieEffects]),
    StoreDevtoolsModule.instrument({logOnly: environment.production}),
    StoreRouterConnectingModule.forRoot(),
    SharedModule,
    CoreModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
