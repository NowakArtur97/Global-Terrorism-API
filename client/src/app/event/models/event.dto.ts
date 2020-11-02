import CityDTO from 'src/app/city/models/city.dto';
import TargetDTO from 'src/app/target/models/target.dto';
import VictimDTO from 'src/app/victim/models/victim.dto';

export default class Event {
  constructor(
    private _summary: string,
    private _motive: string,
    private _date: Date,
    private _isPartOfMultipleIncidents: boolean,
    private _isSuccessful: boolean,
    private _isSuicidal: boolean,
    private _target: TargetDTO,
    private _city: CityDTO,
    private _victim: VictimDTO
  ) {}

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

  public get target(): TargetDTO {
    return this._target;
  }

  public get city(): CityDTO {
    return this._city;
  }

  public get victim(): VictimDTO {
    return this._victim;
  }
}
