import User from '../models/User';

export default interface AuthStoreState {
  user: User;
  authErrorMessages: string[];
}
