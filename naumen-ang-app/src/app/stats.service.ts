import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class StatsService {

  getAmountURL = "http://localhost:8080/api/v1/req-amount"

  constructor(private http:HttpClient) { }

  getRequestsAmount() {
    return this.http.get(this.getAmountURL);
  }
}
