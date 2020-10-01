import User from '../models/user.model';

export default interface AuthStoreState {
  user: User;
  authErrorMessages: string[];
}
