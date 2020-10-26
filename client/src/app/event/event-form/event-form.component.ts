import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-event-form',
  templateUrl: './event-form.component.html',
  styleUrls: ['./event-form.component.css'],
})
export class EventFormComponent implements OnInit {
  eventForm: FormGroup;
  isLoading = false;

  constructor() {}

  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.eventForm = new FormGroup({
      summary: new FormControl('', Validators.required),
      motive: new FormControl('', Validators.required),
      date: new FormControl('', Validators.required),
      isPartOfMultipleIncidents: new FormControl('false', Validators.required),
      isSuccessful: new FormControl('false', Validators.required),
      isSuicidal: new FormControl('false', Validators.required),
    });
  }

  onAddForm(): void {}
}
