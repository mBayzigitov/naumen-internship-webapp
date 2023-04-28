import { Component } from '@angular/core';
import {StatsService} from "../stats.service";

@Component({
  selector: 'app-stats',
  templateUrl: './stats.component.html',
  styleUrls: ['./stats.component.css']
})
export class StatsComponent {

  requestsAmount: number = 0;

  constructor(private statsService: StatsService) {}

  ngOnInit() {
    this.statsService.getRequestsAmount().subscribe(
      (amount: number) => {
        this.requestsAmount = amount;
      }
    );
  }

}
