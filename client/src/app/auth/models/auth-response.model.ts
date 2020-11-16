export default interface AuthResponse {
  readonly token: string;
  readonly expirationTimeInMilliseconds: number;
}
