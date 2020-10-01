export default class ErrorResponse {
  constructor(
    private readonly _errors: string[],
    private readonly _status: number,
    private readonly _timestamp: Date
  ) {}

  public get errors(): string[] {
    return this._errors;
  }

  public get status(): number {
    return this._status;
  }

  public get timestamp(): Date {
    return this._timestamp;
  }
}
