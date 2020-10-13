import City from 'src/app/cities/models/city.model';

export default class Event {
  constructor(
    private readonly _id: number,
    private _summary: string,
    private _motive: string,
    private _date: Date,
    private _isPartOfMultipleIncidents: boolean,
    private _isSuccessful: boolean,
    private _isSuicidal: boolean,
    private _city: City
  ) {}

  public get id(): number {
    return this._id;
  }

  public get summary(): string {
    return this._summary;
  }

  public get motive(): string {
    return this._motive;
  }

  public get date(): Date {
    return this._date;
  }

  public get isPartOfMultipleIncidents(): boolean {
    return this._isPartOfMultipleIncidents;
  }

  public get isSuccessful(): boolean {
    return this._isSuccessful;
  }

  public get isSuicidal(): boolean {
    return this._isSuicidal;
  }

  public get city(): City {
    return this._city;
  }
}
