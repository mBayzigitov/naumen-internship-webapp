import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {PeopleListComponent} from "./people-list/people-list.component";
import {UploadPageComponent} from "./upload-page/upload-page.component";
import {NameInputComponent} from "./name-input/name-input.component";

const routes: Routes = [
  {path: 'people', component: PeopleListComponent},
  {path: 'upload', component: UploadPageComponent },
  {path: '', redirectTo: 'upload', pathMatch: 'full'},
  {path: 'name-input', component: NameInputComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
