import LoginData from '../../models/login-data.model';
import User from '../../models/user.model';
import * as AuthActions from '../auth.actions';
import authReducer from '../auth.reducer';
import AuthStoreState from '../auth.store.state';

const initialState: AuthStoreState = {
  user: null,
  authErrorMessages: [],
};

const initialStateWithErrors: AuthStoreState = {
  user: null,
  authErrorMessages: ['ERROR'],
};

const initialStateWithUser: AuthStoreState = {
  user: new User('token', 36000),
  authErrorMessages: [],
};

const initialStateWithUserAndErrors: AuthStoreState = {
  user: new User('token', 36000),
  authErrorMessages: ['ERROR'],
};

describe('authReducer', () => {
  describe('AuthStoreState.loginUserStart', () => {
    it('should remove error messages when login started', () => {
      const authErrorMessages = [];
      const loginData = new LoginData('user', 'password');
      const action = AuthActions.loginUserStart({ loginData });
      const actualState = authReducer(initialStateWithErrors, action);
      const expectedState = {
        ...initialStateWithErrors,
        authErrorMessages,
      };

      expect(actualState).toEqual(expectedState);
      expect(actualState.authErrorMessages.length).toBe(0);
    });
  });

  describe('AuthStoreState.authenticateUserSuccess', () => {
    it('should authenticate user on success', () => {
      const user = new User('token', 36000);
      const authErrorMessages = [];
      const action = AuthActions.authenticateUserSuccess({ user });
      const actualState = authReducer(initialState, action);
      const expectedState = { ...initialState, user, authErrorMessages };

      expect(actualState).toEqual(expectedState);
      expect(actualState.user).toEqual(user);
      expect(actualState.authErrorMessages.length).toBe(0);
    });

    it('should authenticate user and remove error messages on success', () => {
      const user = new User('token', 36000);
      const authErrorMessages = [];
      const action = AuthActions.authenticateUserSuccess({ user });
      const actualState = authReducer(initialStateWithErrors, action);
      const expectedState = {
        ...initialStateWithErrors,
        user,
        authErrorMessages,
      };

      expect(actualState).toEqual(expectedState);
      expect(actualState.user).toEqual(user);
      expect(actualState.authErrorMessages.length).toBe(0);
    });
  });

  describe('AuthStoreState.authenticateUserFailure', () => {
    it('should store error messages on failure', () => {
      const authErrorMessages = ['error'];
      const action = AuthActions.authenticateUserFailure({ authErrorMessages });
      const actualState = authReducer(initialState, action);
      const expectedState = {
        ...initialState,
        authErrorMessages,
      };

      expect(actualState).toEqual(expectedState);
      expect(actualState.authErrorMessages).toEqual(authErrorMessages);
    });
  });

  describe('AuthStoreState.logoutUser', () => {
    it('should logout user', () => {
      const user = null;
      const authErrorMessages = [];
      const action = AuthActions.logoutUser();
      const actualState = authReducer(initialStateWithUser, action);
      const expectedState = {
        ...initialStateWithUser,
        user,
        authErrorMessages,
      };

      expect(actualState).toEqual(expectedState);
      expect(actualState.user).toBeNull();
      expect(actualState.authErrorMessages.length).toBe(0);
    });

    it('should logout user and remove error messages', () => {
      const user = null;
      const authErrorMessages = [];
      const action = AuthActions.logoutUser();
      const actualState = authReducer(initialStateWithUserAndErrors, action);
      const expectedState = {
        ...initialStateWithUser,
        user,
        authErrorMessages,
      };

      expect(actualState).toEqual(expectedState);
      expect(actualState.user).toBeNull();
      expect(actualState.authErrorMessages.length).toBe(0);
    });
  });
});
