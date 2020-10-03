export default class User {
  constructor(
    private readonly _token: string,
    private readonly _expirationDate: Date
  ) {}

  public get token(): string {
    return this._token;
  }

  public get expirationDate(): Date {
    return this._expirationDate;
  }
}
