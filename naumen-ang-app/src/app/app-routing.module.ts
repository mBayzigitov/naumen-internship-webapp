import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {PeopleListComponent} from "./people-list/people-list.component";
import {UploadPageComponent} from "./upload-page/upload-page.component";

const routes: Routes = [
  {path: 'people', component: PeopleListComponent},
  {path: 'upload', component: UploadPageComponent },
  {path: '', redirectTo: 'upload', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
