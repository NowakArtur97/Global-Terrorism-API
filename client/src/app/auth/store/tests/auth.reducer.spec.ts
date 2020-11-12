import LoginData from '../../models/login-data.model';
import RegistrationData from '../../models/registration-data.model';
import User from '../../models/user.model';
import * as AuthActions from '../auth.actions';
import authReducer, { AuthStoreState } from '../auth.reducer';

const initialState: AuthStoreState = {
  user: null,
  authErrorMessages: [],
  isLoading: false,
};

const initialStateWithErrors: AuthStoreState = {
  user: null,
  authErrorMessages: ['ERROR'],
  isLoading: true,
};

const initialStateWithUser: AuthStoreState = {
  user: new User('token', new Date(Date.now() + 36000000)),
  authErrorMessages: [],
  isLoading: true,
};

const initialStateWithUserAndErrors: AuthStoreState = {
  user: new User('token', new Date(Date.now() + 36000000)),
  authErrorMessages: ['ERROR'],
  isLoading: true,
};

describe('authReducer', () => {
  describe('AuthStoreState.loginUserStart', () => {
    it('should remove display loader when login started', () => {
      const isLoading = true;
      const loginData = new LoginData('user', 'password');
      const action = AuthActions.loginUserStart({ loginData });
      const actualState = authReducer(initialStateWithErrors, action);
      const expectedState = {
        ...initialState,
        isLoading,
      };

      expect(actualState).toEqual(expectedState);
      expect(actualState.isLoading).toBeTrue();
    });

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

  describe('AuthStoreState.registerUserStart', () => {
    it('should show loader when registration started', () => {
      const isLoading = true;
      const registrationData = new RegistrationData(
        'user',
        'email',
        'password',
        'password'
      );
      const action = AuthActions.registerUserStart({ registrationData });
      const actualState = authReducer(initialStateWithErrors, action);
      const expectedState = {
        ...initialState,
        isLoading,
      };

      expect(actualState).toEqual(expectedState);
      expect(actualState.isLoading).toBeTrue();
    });

    it('should remove error messages when login started', () => {
      const authErrorMessages = [];
      const registrationData = new RegistrationData(
        'user',
        'email',
        'password',
        'password'
      );
      const action = AuthActions.registerUserStart({ registrationData });
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
      const user = new User('token', new Date(Date.now() + 36000000));
      const authErrorMessages = [];
      const action = AuthActions.authenticateUserSuccess({ user });
      const actualState = authReducer(initialState, action);
      const expectedState = { ...initialState, user, authErrorMessages };

      expect(actualState).toEqual(expectedState);
      expect(actualState.user).toEqual(user);
      expect(actualState.authErrorMessages.length).toBe(0);
      expect(actualState.isLoading).toBeFalse();
    });

    it('should authenticate user, remove error messages and hide loader on success', () => {
      const user = new User('token', new Date(Date.now() + 36000000));
      const authErrorMessages = [];
      const isLoading = false;
      const action = AuthActions.authenticateUserSuccess({ user });
      const actualState = authReducer(initialStateWithErrors, action);
      const expectedState = {
        ...initialStateWithErrors,
        user,
        authErrorMessages,
        isLoading,
      };

      expect(actualState).toEqual(expectedState);
      expect(actualState.user).toEqual(user);
      expect(actualState.authErrorMessages.length).toBe(0);
      expect(actualState.isLoading).toBeFalse();
    });
  });

  describe('AuthStoreState.authenticateUserFailure', () => {
    it('should store error messages and hide loader on failure', () => {
      const authErrorMessages = ['error'];
      const isLoading = false;
      const action = AuthActions.authenticateUserFailure({ authErrorMessages });
      const actualState = authReducer(initialState, action);
      const expectedState = {
        ...initialState,
        authErrorMessages,
        isLoading,
      };

      expect(actualState).toEqual(expectedState);
      expect(actualState.authErrorMessages).toEqual(authErrorMessages);
      expect(actualState.isLoading).toBeFalse();
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
