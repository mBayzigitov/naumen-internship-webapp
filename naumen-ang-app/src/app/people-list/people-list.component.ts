import { Component } from '@angular/core';
import { Person } from "../person";
import {PersonService} from "../person.service";

@Component({
  selector: 'app-people-list',
  templateUrl: './people-list.component.html',
  styleUrls: ['./people-list.component.css']
})
export class PeopleListComponent {

  people!: Person[];

  constructor(private personService: PersonService) {
  }

  ngOnInit(): void {
    this.getPeople();
  }

  private getPeople() {
    this.personService.getPeopleList().subscribe(data => {
      this.people = data;
    })
  }

  clear() {
    if (confirm("Вы действительно хотите удалить все записи?")) {
      this.personService.clearPeopleList();
      this.getPeople();
    }
  }
}
