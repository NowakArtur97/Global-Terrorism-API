export default class RegistrationData {
  constructor(
    private readonly _userName: string,
    private readonly _email: string,
    private readonly _password: string,
    private readonly _matchingPassword: string
  ) {}

  public get userName(): string {
    return this._userName;
  }

  public get email(): string {
    return this._email;
  }

  public get password(): string {
    return this._password;
  }

  public get matchingPassword(): string {
    return this._matchingPassword;
  }
}
