import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-victim-form',
  templateUrl: './victim-form.component.html',
  styleUrls: ['./victim-form.component.css'],
})
export class VictimFormComponent implements OnInit {
  victimForm: FormGroup;

  constructor() {}

  ngOnInit(): void {
    this.initForm();
  }

  initForm() {
    this.victimForm = new FormGroup({
      totalNumberOfFatalities: new FormControl('', Validators.required),
      numberOfPerpetratorFatalities: new FormControl('', Validators.required),
      totalNumberOfInjured: new FormControl('', Validators.required),
      numberOfPerpetratorInjured: new FormControl('', Validators.required),
      valueOfPropertyDamage: new FormControl('', Validators.required),
    });
  }
}
