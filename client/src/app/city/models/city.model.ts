import Province from 'src/app/province/models/province.model';

export default class City {
  constructor(
    private readonly _id: number,
    private _name: string,
    private _latitude: number,
    private _longitude: number,
    private _province: Province
  ) {}

  public get id(): number {
    return this._id;
  }

  public get name(): string {
    return this._name;
  }

  public get latitude(): number {
    return this._latitude;
  }

  public get longitude(): number {
    return this._longitude;
  }

  public get province(): Province {
    return this._province;
  }
}
