export default class User {
  constructor(private readonly _token: string) {}

  public get token(): string {
    return this._token;
  }
}
