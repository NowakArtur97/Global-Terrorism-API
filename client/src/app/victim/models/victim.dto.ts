export default class VictimDTO {
  constructor(
    private _totalNumberOfFatalities: number,
    private _numberOfPerpetratorFatalities: number,
    private _totalNumberOfInjured: number,
    private _numberOfPerpetratorInjured: number,
    private _valueOfPropertyDamage: number
  ) {}

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
