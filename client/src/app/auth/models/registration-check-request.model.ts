export default class RegistrationCheckRequest {
  constructor(
    private readonly _userName: string,
    private readonly _email: string
  ) {}

  public get userName(): string {
    return this._userName;
  }

  public get email(): string {
    return this._email;
  }
}
