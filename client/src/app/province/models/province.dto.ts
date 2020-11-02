import CountryDTO from 'src/app/country/models/country.dto';

export default class ProvinceDTO {
  constructor(private _name: string, private _country: CountryDTO) {}

  public get name(): string {
    return this._name;
  }

  public get country(): CountryDTO {
    return this._country;
  }
}
