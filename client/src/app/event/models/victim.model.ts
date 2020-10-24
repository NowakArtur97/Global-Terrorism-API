export default class Victim {
  constructor(
    private readonly _id: number,
    private _totalNumberOfFatalities: number,
    private _numberOfPerpetratorFatalities: number,
    private _totalNumberOfInjured: number,
    private _numberOfPerpetratorInjured: number,
    private _valueOfPropertyDamage: number
  ) {}

  public get id(): number {
    return this._id;
  }

  public get totalNumberOfFatalities(): number {
    return this._totalNumberOfFatalities;
  }

  public get numberOfPerpetratorFatalities(): number {
    return this._numberOfPerpetratorFatalities;
  }

  public get totalNumberOfInjured(): number {
    return this._totalNumberOfInjured;
  }

  public get numberOfPerpetratorInjured(): number {
    return this._numberOfPerpetratorInjured;
  }

  public get valueOfPropertyDamage(): number {
    return this._valueOfPropertyDamage;
  }
}
