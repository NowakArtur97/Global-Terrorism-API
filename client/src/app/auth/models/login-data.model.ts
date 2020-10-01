export default class LoginData {
  constructor(
    private readonly _userNameOrEmail: string,
    private readonly _password: string
  ) {}

  public get userNameOrEmail(): string {
    return this._userNameOrEmail;
  }

  public get password(): string {
    return this._password;
  }
}
