export default class RegistrationCheckResponse {
  constructor(
    private readonly _isUserNameAvailable: boolean,
    private readonly _isEmailAvailable: boolean
  ) {}

  public get isUserNameAvailable(): boolean {
    return this._isUserNameAvailable;
  }

  public get isEmailAvailable(): boolean {
    return this._isEmailAvailable;
  }
}
