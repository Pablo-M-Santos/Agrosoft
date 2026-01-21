export interface Employee {
  id?: string;
  fullName: string;
  email: string;
  cpf: string;
  rg?: string;          
  phone?: string;
  birthDate?: Date;
  address?: string;
  photoUrl?: string;
  driverLicenseCategory?: string;
  workArea: string;
  salary: number;
  hireDate: Date;
  terminationDate?: Date;
  contractType: 'CLT' | 'PJ';
  status: 'ACTIVE' | 'INACTIVE' | 'ON_LEAVE' | 'TERMINATED';
}