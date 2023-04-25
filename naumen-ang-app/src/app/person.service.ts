import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Person} from "./person";

@Injectable({
  providedIn: 'root'
})
export class PersonService {

  private baseURL = "http://localhost:8080/api/v1/people";

  constructor(private httpClient: HttpClient) { }

  getPeopleList(): Observable<Person[]> {
    return this.httpClient.get<Person[]>(`${this.baseURL}`);
  }
}
