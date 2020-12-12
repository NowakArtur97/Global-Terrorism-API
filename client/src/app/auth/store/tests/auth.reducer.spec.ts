import LoginData from '../../models/login-data.model';
import RegistrationData from '../../models/registration-data.model';
import * as AuthActions from '../auth.actions';
import authReducer, { AuthStoreState } from '../auth.reducer';

const state: AuthStoreState = {
  user: null,
  authErrorMessages: [],
  isLoading: false,
  userLocation: null,
};
const stateWithErrorsAndLoading: AuthStoreState = {
  user: null,
  authErrorMessages: ['ERROR'],
  isLoading: true,
  userLocation: null,
};
const stateWithUser: AuthStoreState = {
  user: {
    token: 'secret token',
    expirationDate: new Date(Date.now() + 36000000),
  },
  authErrorMessages: [],
  isLoading: true,
  userLocation: null,
};

describe('authReducer', () => {
  describe('AuthStoreState.loginUserStart', () => {
    it('should remove display loader when login started', () => {
      const isLoading = true;
      const loginData: LoginData = {
        userNameOrEmail: 'user',
        password: 'password',
      };
      const action = AuthActions.loginUserStart({ loginData });
      const actualState = authReducer(stateWithErrorsAndLoading, action);
      const expectedState = {
        ...state,
        isLoading,
      };

      expect(actualState).toEqual(expectedState);
      expect(actualState.isLoading).toBeTrue();
    });

    it('should remove error messages when login started', () => {
      const authErrorMessages = [];
      const loginData: LoginData = {
        userNameOrEmail: 'user',
        password: 'password',
      };
      const action = AuthActions.loginUserStart({ loginData });
      const actualState = authReducer(stateWithErrorsAndLoading, action);
      const expectedState = {
        ...stateWithErrorsAndLoading,
        authErrorMessages,
      };

      expect(actualState).toEqual(expectedState);
      expect(actualState.authErrorMessages.length).toBe(0);
    });
  });

  describe('AuthStoreState.registerUserStart', () => {
    it('should show loader when registration started', () => {
      const isLoading = true;
      const registrationData: RegistrationData = {
        userName: 'username',
        email: 'email@email.com',
        password: 'password',
        matchingPassword: 'password',
      };
      const action = AuthActions.registerUserStart({ registrationData });
      const actualState = authReducer(stateWithErrorsAndLoading, action);
      const expectedState = {
        ...state,
        isLoading,
      };

      expect(actualState).toEqual(expectedState);
      expect(actualState.isLoading).toBeTrue();
    });

    it('should remove error messages when login started', () => {
      const authErrorMessages = [];
      const registrationData: RegistrationData = {
        userName: 'username',
        email: 'email@email.com',
        password: 'password',
        matchingPassword: 'password',
      };
      const action = AuthActions.registerUserStart({ registrationData });
      const actualState = authReducer(stateWithErrorsAndLoading, action);
      const expectedState = {
        ...stateWithErrorsAndLoading,
        authErrorMessages,
      };

      expect(actualState).toEqual(expectedState);
      expect(actualState.authErrorMessages.length).toBe(0);
    });
  });

  describe('AuthStoreState.authenticateUserSuccess', () => {
    it('should authenticate user on success', () => {
      const user = {
        token: 'secret token',
        expirationDate: new Date(Date.now() + 36000000),
      };
      const authErrorMessages = [];
      const action = AuthActions.authenticateUserSuccess({ user });
      const actualState = authReducer(state, action);
      const expectedState = { ...state, user, authErrorMessages };

      expect(actualState).toEqual(expectedState);
      expect(actualState.user).toEqual(user);
      expect(actualState.authErrorMessages.length).toBe(0);
      expect(actualState.isLoading).toBeFalse();
    });

    it('should authenticate user, remove error messages and hide loader on success', () => {
      const user = {
        token: 'secret token',
        expirationDate: new Date(Date.now() + 36000000),
      };
      const authErrorMessages = [];
      const isLoading = false;
      const action = AuthActions.authenticateUserSuccess({ user });
      const actualState = authReducer(stateWithErrorsAndLoading, action);
      const expectedState = {
        ...stateWithErrorsAndLoading,
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
      const actualState = authReducer(state, action);
      const expectedState = {
        ...state,
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
      const actualState = authReducer(stateWithUser, action);
      const expectedState = {
        ...stateWithUser,
        user,
        authErrorMessages,
      };

      expect(actualState).toEqual(expectedState);
      expect(actualState.user).toBeNull();
      expect(actualState.authErrorMessages.length).toBe(0);
    });

    it('should logout user and remove error messages', () => {
      const stateWithUserAndErrors: AuthStoreState = {
        user: {
          token: 'secret token',
          expirationDate: new Date(Date.now() + 36000000),
        },
        authErrorMessages: ['ERROR'],
        isLoading: true,
        userLocation: null,
      };
      const user = null;
      const authErrorMessages = [];
      const action = AuthActions.logoutUser();
      const actualState = authReducer(stateWithUserAndErrors, action);
      const expectedState = {
        ...stateWithUser,
        user,
        authErrorMessages,
      };

      expect(actualState).toEqual(expectedState);
      expect(actualState.user).toBeNull();
      expect(actualState.authErrorMessages.length).toBe(0);
    });
  });

  describe('AuthStoreState.startFillingOutForm', () => {
    it('should remove error messages', () => {
      const stateWithErrors: AuthStoreState = {
        user: null,
        authErrorMessages: ['ERROR'],
        isLoading: false,
        userLocation: null,
      };
      const action = AuthActions.startFillingOutForm();
      const actualState = authReducer(stateWithErrors, action);
      const expectedState = {
        ...state,
        authErrorMessages: [],
      };

      expect(actualState).toEqual(expectedState);
      expect(actualState.authErrorMessages).toEqual([]);
      expect(actualState.authErrorMessages.length).toBe(0);
    });
  });

  describe('AuthStoreState.setUserLocation', () => {
    it('should set user lcoation', () => {
      const userLocation: L.LatLngExpression = [10, 20];
      const stateWithUserLocation: AuthStoreState = {
        user: null,
        authErrorMessages: [],
        isLoading: false,
        userLocation,
      };
      const action = AuthActions.setUserLocation({ userLocation });
      const actualState = authReducer(state, action);
      const expectedState = {
        ...stateWithUserLocation,
      };

      expect(actualState).toEqual(expectedState);
      expect(actualState.userLocation).toEqual(userLocation);
    });
  });
});
