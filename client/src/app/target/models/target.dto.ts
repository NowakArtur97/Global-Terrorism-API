import CountryDTO from 'src/app/country/models/country.dto';

export default class TargetDTO {
  constructor(private _target: string, private _country: CountryDTO) {}

  public get target(): string {
    return this._target;
  }

  public get country(): CountryDTO {
    return this._country;
  }
}
