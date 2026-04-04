export interface Animal {
  id: string;
  name: string;
  weight: number;
  entryDate?: string;
  createdAt?: string;

  status: string;

  animalTypeId: string;
  animalTypeName: string;
  animalType?: {
    id: string;
    name: string;
  };

  responsibleEmployeeId?: string;
  responsibleEmployeeName?: string;
  responsibleEmployee?: {
    id: string;
    fullName: string;
  };

  statusText?: string;
  statusColor?: string;
  statusTextColor?: string;
}
