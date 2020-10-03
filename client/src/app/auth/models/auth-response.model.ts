export default class AuthResponse {
  constructor(
    private readonly _token: string,
    private readonly _expirationDateInMilliseconds: number
  ) {}

  public get token(): string {
    return this._token;
  }

  public get expirationDateInMilliseconds(): number {
    return this._expirationDateInMilliseconds;
  }
}
