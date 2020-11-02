import ProvinceDTO from 'src/app/province/models/province.dto';

export default class CityDTO {
  constructor(
    private _name: string,
    private _latitude: number,
    private _longitude: number,
    private _province: ProvinceDTO
  ) {}

  public get name(): string {
    return this._name;
  }

  public get latitude(): number {
    return this._latitude;
  }

  public get longitude(): number {
    return this._longitude;
  }

  public get province(): ProvinceDTO {
    return this._province;
  }
}
