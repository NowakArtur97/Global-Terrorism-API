export default class AuthResponse {
  constructor(
    private readonly _token: string,
    private readonly _expirationTimeInMilliseconds: number
  ) {}

  public get token(): string {
    return this._token;
  }

  public get expirationTimeInMilliseconds(): number {
    return this._expirationTimeInMilliseconds;
  }
}
