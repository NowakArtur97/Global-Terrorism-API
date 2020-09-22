export default class LoginData {
  constructor(private _userNameOrEmail: string, private _password: string) {}

  public get userNameOrEmail(): string {
    return this._userNameOrEmail;
  }

  public set userNameOrEmail(userNameOrEmail: string) {
    this._userNameOrEmail = userNameOrEmail;
  }

  public get password(): string {
    return this._password;
  }

  public set password(password: string) {
    this._password = password;
  }
}