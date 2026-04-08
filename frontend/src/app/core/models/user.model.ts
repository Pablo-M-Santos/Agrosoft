export type AccessLevel = 'ADMIN' | 'MANAGER' | 'OPERATOR' | 'FINANCIAL' | 'VIEWER';

export interface User {
  id: string;
  email: string;
  accessLevel: AccessLevel;
  lastLogin?: string | null;
  createdAt?: string;
  updatedAt?: string;
  active: boolean;
}

export interface CreateUserPayload {
  email: string;
  password: string;
  accessLevel: AccessLevel;
}

export interface UpdateUserPayload {
  email: string;
  accessLevel: AccessLevel;
  password?: string;
}
