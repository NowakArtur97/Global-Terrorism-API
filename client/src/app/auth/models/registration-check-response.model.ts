export default class RegistrationCheckResponse {
  constructor(
    private readonly _isUserNameAvailable: boolean,
    private readonly _isEmailAvailable: boolean
  ) {}
}
