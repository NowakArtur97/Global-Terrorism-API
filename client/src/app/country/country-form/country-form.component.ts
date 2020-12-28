import { Component, forwardRef, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, NG_VALIDATORS, NG_VALUE_ACCESSOR } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import { AbstractFormComponent } from 'src/app/common/components/abstract-form.component';
import CommonValidators from 'src/app/common/validators/common.validator';
import { selectEventToUpdate } from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import Event from '../../event/models//event.model';
import Country from '../models/country.model';
import CountryService from '../services/country.service';

@Component({
  selector: 'app-country-form',
  templateUrl: './country-form.component.html',
  styleUrls: ['./country-form.component.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => CountryFormComponent),
      multi: true,
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => CountryFormComponent),
      multi: true,
    },
  ],
})
export class CountryFormComponent
  extends AbstractFormComponent
  implements OnInit, OnDestroy {
  private countriesSubscription$: Subscription;
  countries: Country[] = [];

  constructor(
    protected store: Store<AppStoreState>,
    private countryService: CountryService
  ) {
    super(store);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.countriesSubscription$ = this.countryService
      .getAll()
      .subscribe(
        (countriesResponse) => (this.countries = countriesResponse.content)
      );
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
    this.countriesSubscription$?.unsubscribe();
  }

  initForm(): void {
    let name = '';

    this.updateSubscription$.add(
      this.store.select(selectEventToUpdate).subscribe((event: Event) => {
        if (event?.city?.province?.country) {
          name = event.city.province.country.name;
        }
      })
    );

    this.formGroup = new FormGroup({
      name: new FormControl(name, [CommonValidators.notBlank]),
    });
  }

  get name(): AbstractControl {
    return this.formGroup.get('name');
  }
}
