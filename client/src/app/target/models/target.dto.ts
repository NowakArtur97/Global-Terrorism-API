import Country from 'src/app/country/models/country.model';

export default class TargetDTO {
  constructor(private _target: string, private _country: Country) {}

  public get target(): string {
    return this._target;
  }

  public get country(): Country {
    return this._country;
  }
}
