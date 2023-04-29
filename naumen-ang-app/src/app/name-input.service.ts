import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Person} from "./person";

@Injectable({
  providedIn: 'root'
})
export class NameInputService {

  getPersonURL = 'http://localhost:8080/api/v1/getPerson/';

  constructor(private http:HttpClient) { }

  getPerson(passedName: string): Observable<Person> {
    return this.http.get<Person>(this.getPersonURL + passedName);
  }
}
