import Country from 'src/app/country/models/country.model';

export default class Target {
  constructor(
    private readonly _id: number,
    private _target: string,
    private _country: Country
  ) {}

  public get id(): number {
    return this._id;
  }

  public get target(): string {
    return this._target;
  }

  public get country(): Country {
    return this._country;
  }
}
