export default class Country {
  constructor(private readonly _id: number, private _name: string) {}

  public get id(): number {
    return this._id;
  }

  public get name(): string {
    return this._name;
  }
}
