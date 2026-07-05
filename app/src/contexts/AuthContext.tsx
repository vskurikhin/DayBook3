import * as React from 'react';
import PropTypes from 'prop-types';

import { STATUS } from '../utils/utils';

interface AuthState {
  user: User;
  token: string | null;
  expires_at: string | null;
  isAuthenticated: boolean;
  status: string;
  verifyingToken: boolean;
}

interface AuthContextType extends AuthState {
    login: (
        user: User,
        token: string,
        expires_at: string
    ) => void;

    logout: () => void;

    updateUser: (
        user: User
    ) => void;

    setAuthenticationStatus: (
        status: string
    ) => void;
}

interface User {
  name: string;
  email?: string;
}

type AuthAction =
  | {
      type: "login";
      payload: {
        data: {
          user: User;
          token: string;
          expires_at: string;
        };
      };
    }
  | {
      type: "logout";
    }
  | {
      type: "updateUser";
      payload: {
        data: {
          user: User;
        };
      };
    }
  | {
      type: "status";
      payload: {
        data: {
          status: string;
        };
      };
    };

type Props = {
  children: React.ReactNode;
};

const initialState: AuthState = {
  user: {} as User,
  token: null,
  expires_at: null,
  isAuthenticated: false,
  status: STATUS.IDLE,
  verifyingToken: false,
};

const AuthContext = React.createContext<AuthContextType>({
  ...initialState,
  login: (user: User, token: string, expires_at: string) => {},
  logout: () => {},
  updateUser: () => {},
  setAuthenticationStatus: () => {},
});

const authReducer = (state: AuthState, action: AuthAction): AuthState => {
console.log("authReducer() state: " + state + " action: " + action)
  switch (action.type) {
    case 'login': {
      return {
        user: action.payload.data.user,
        token: action.payload.data.token,
        expires_at: action.payload.data.expires_at,
        isAuthenticated: true,
        verifyingToken: false,
        status: STATUS.SUCCEEDED,
      };
    }
    case 'logout': {
      return {
        ...initialState,
        status: STATUS.IDLE,
      };
    }
    case 'updateUser': {
      return {
        ...state,
        user: action.payload.data.user,
      };
    }
    case 'status': {
      return {
        ...state,
        status: action.payload.data.status,
      };
    }
    default: {
      const _exhaustiveCheck: never = action;
      throw new Error(`Unhandled action type`);
    }
  }
};

const AuthProvider = ({ children }: Props) => {
  const [state, dispatch] = React.useReducer(authReducer, initialState);

  const login = React.useCallback((user: User, token: string, expires_at: string) => {
    dispatch({
      type: 'login',
      payload: {
        data: {
          user,
          token,
          expires_at,
        },
      },
    });
  }, []);

  const logout = React.useCallback(() => {
    dispatch({
      type: 'logout',
    });
  }, []);

  const updateUser = React.useCallback((user: User) => {
    dispatch({
      type: 'updateUser',
      payload: {
        data: {
          user,
        },
      },
    });
  }, []);

  const setAuthenticationStatus = React.useCallback((status: string) => {
      console.log("setAuthenticationStatus: " + status)
    dispatch({
      type: 'status',
      payload: {
        data: {
          status,
        },
      },
    });
  }, []);

  const value = React.useMemo(
    () => ({ ...state, login, logout, updateUser, setAuthenticationStatus }),
    [state, setAuthenticationStatus, login, logout, updateUser]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

const useAuth = () => {
  const context = React.useContext(AuthContext);

  if (context === undefined) {
    throw new Error('useAuth must be used within a AuthProvider');
  }

  return context;
};

AuthProvider.propTypes = {
  children: PropTypes.element.isRequired,
};

export { AuthProvider, useAuth };