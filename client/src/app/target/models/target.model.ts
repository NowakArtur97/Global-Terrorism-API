export default class Target {
  constructor(private readonly _id: number, private _target: string) {}

  public get id(): number {
    return this._id;
  }

  public get target(): string {
    return this._target;
  }
}
