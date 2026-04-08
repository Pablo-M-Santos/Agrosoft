import { AccessLevel, User } from '../models/user.model';

export interface LoginPayload {
  email: string;
  password: string;
}

export interface RegisterPayload {
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresAt: string;
  userId: string;
  email: string;
  accessLevel: AccessLevel;
}

export interface AuthState {
  token: string | null;
  me: User | null;
}
