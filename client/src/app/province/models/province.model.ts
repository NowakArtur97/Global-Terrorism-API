import Country from 'src/app/country/models/country.model';

export default class Province {
  constructor(
    private readonly _id: number,
    private _name: string,
    private _country: Country
  ) {}

  public get id(): number {
    return this._id;
  }

  public get name(): string {
    return this._name;
  }

  public get country(): Country {
    return this._country;
  }
}
