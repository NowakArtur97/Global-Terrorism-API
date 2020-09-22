export default class LoginData {
  constructor(private _userOrEmail: string, private _password: string) {}

  public get userOrEmail(): string {
    return this._userOrEmail;
  }

  public set userOrEmail(userOrEmail: string) {
    this._userOrEmail = userOrEmail;
  }

  public get password(): string {
    return this._password;
  }

  public set password(password: string) {
    this._password = password;
  }
}
