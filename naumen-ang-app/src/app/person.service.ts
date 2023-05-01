import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Person} from "./person";

@Injectable({
  providedIn: 'root'
})
export class PersonService {

  status = '';
  private baseURL = "http://localhost:8080/api/v1/";

  constructor(private httpClient: HttpClient) { }

  getPeopleList(): Observable<Person[]> {
    return this.httpClient.get<Person[]>(this.baseURL + "people");
  }

  clearPeopleList(): boolean {
    this.httpClient.delete(this.baseURL + "clear", {responseType: "text"}).subscribe(
      (response) => {
        this.status = response;
      }
    );
    return true;
  }
}
