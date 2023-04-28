import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { PeopleListComponent } from './people-list/people-list.component';
import {HttpClientModule} from "@angular/common/http";
import { UploadPageComponent } from './upload-page/upload-page.component';
import { NameInputComponent } from './name-input/name-input.component';
import {FormsModule} from "@angular/forms";
import { StatsComponent } from './stats/stats.component';

@NgModule({
  declarations: [
    AppComponent,
    PeopleListComponent,
    UploadPageComponent,
    NameInputComponent,
    StatsComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        HttpClientModule,
        FormsModule
    ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
