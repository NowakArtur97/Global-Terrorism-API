import { Component, forwardRef, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, NG_VALIDATORS, NG_VALUE_ACCESSOR } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import { AbstractFormComponent } from 'src/app/common/components/abstract-form.component';
import CommonValidators from 'src/app/common/validators/common.validator';
import { selectEventToUpdate } from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import Event from '../../event/models//event.model';
import Province from '../models/province.model';
import ProvinceService from '../services/province.service';

@Component({
  selector: 'app-province-form',
  templateUrl: './province-form.component.html',
  styleUrls: ['./province-form.component.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => ProvinceFormComponent),
      multi: true,
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => ProvinceFormComponent),
      multi: true,
    },
  ],
})
export class ProvinceFormComponent
  extends AbstractFormComponent
  implements OnInit, OnDestroy {
  private provincesSubscription$: Subscription;
  provinces: Province[] = [];

  constructor(
    protected store: Store<AppStoreState>,
    private provinceService: ProvinceService
  ) {
    super(store);
  }
  ngOnInit(): void {
    super.ngOnInit();
    this.provincesSubscription$ = this.provinceService
      .getAll()
      .subscribe(
        (provincesResponse) => (this.provinces = provincesResponse.content)
      );
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
    this.provincesSubscription$?.unsubscribe();
  }

  initForm(): void {
    let name = '';

    this.updateSubscription$.add(
      this.store.select(selectEventToUpdate).subscribe((event: Event) => {
        if (event?.city?.province) {
          name = event.city.province.name;
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

  selectProvince(event: MatAutocompleteSelectedEvent): void {
    if (!event.option) {
      return;
    }
    this.name.setValue(event.option.value);
  }
}
