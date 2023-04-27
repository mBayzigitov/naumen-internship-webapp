import { Component } from '@angular/core';
import {NameInputService} from "../name-input.service";
import {Person} from "../person";

@Component({
  selector: 'app-name-input',
  templateUrl: './name-input.component.html',
  styleUrls: ['./name-input.component.css']
})
export class NameInputComponent {

  recievedPerson: Person = null;
  flag: boolean = false;
  inputName = '';
  response = '';
  error: string = null;

  constructor(private nameInputService: NameInputService) {}

  ngOnInit(): void {
  }

  onSubmit() {
    this.nameInputService.getPerson(this.inputName).subscribe(
      (person) => {
        if (person == null) {
          this.flag = true;
          this.response = "No such name found";
        } else {
          this.flag = true;
          this.recievedPerson = person;
          this.response = this.recievedPerson.age.toString();
        }
      },
      (error) => {
        this.error = error.error;
        this.response = '';
        this.recievedPerson = null;
        this.flag = false;
      }
    );
  }

}
