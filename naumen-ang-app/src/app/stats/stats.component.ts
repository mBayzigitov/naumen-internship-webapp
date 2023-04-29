import { Component } from '@angular/core';
import {StatsService} from "../stats.service";
import {PersonStats} from "../person-stats";
import {Person} from "../person";

@Component({
  selector: 'app-stats',
  templateUrl: './stats.component.html',
  styleUrls: ['./stats.component.css']
})
export class StatsComponent {

  peopleStat!: PersonStats[];
  oldest!: Person;
  requestsAmount: number = 0;

  constructor(private statsService: StatsService) {}

  ngOnInit() {
    this.statsService.getRequestsAmount().subscribe(
      (amount: number) => {
        this.requestsAmount = amount;

        if (this.requestsAmount != null || this.requestsAmount != 0)
          this.getPeopleStat();
          this.getOldestPerson();
      }
    );
  }

  private getPeopleStat()  {
    this.statsService.getPeopleStat().subscribe(
      (data) => {
        this.peopleStat = data;
      }
    );
  }

  private getOldestPerson() {
    this.statsService.getOldestPerson().subscribe(
      (person) => {
        this.oldest = person;
      }
    )
  }

}
