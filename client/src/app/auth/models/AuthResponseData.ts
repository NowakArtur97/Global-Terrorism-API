export default class AuthResponse {
  constructor(private readonly _token: string) {}

  public get token(): string {
    return this._token;
  }
}
