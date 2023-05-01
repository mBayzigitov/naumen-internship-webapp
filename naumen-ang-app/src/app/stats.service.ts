import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {PersonStats} from "./person-stats";
import {Person} from "./person";

@Injectable({
  providedIn: 'root'
})
export class StatsService {

  getAmountURL = "http://localhost:8080/api/v1/req-amount";
  getStatURL = "http://localhost:8080/api/v1/freq";
  getOldestURL = "http://localhost:8080/api/v1/oldest";

  constructor(private http:HttpClient) { }

  getRequestsAmount() {
    return this.http.get(this.getAmountURL);
  }

  getPeopleStat() {
    return this.http.get<PersonStats[]>(this.getStatURL);
  }

  getOldestPeople() {
    return this.http.get<Person[]>(this.getOldestURL);
  }
}
