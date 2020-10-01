export default class RegistrationCheckRequest {
  constructor(
    private readonly _isUserNameAvailable: boolean,
    private readonly _isEmailAvailable: boolean
  ) {}
}
