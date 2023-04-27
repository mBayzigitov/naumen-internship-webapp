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
    if (this.inputName.length == 0) {
      this.error = 'Пожалуйста, не оставляйте поле пустым';
      return;
    }

    this.inputName = this.inputName.replace(/[^a-zа-яA-ZА-ЯёЁ 0-9]/g, '')

    this.nameInputService.getPerson(this.inputName).subscribe(
      (person) => {
        this.error = '';
        this.flag = true;
        this.recievedPerson = person;
        this.response = this.recievedPerson.age.toString();
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
