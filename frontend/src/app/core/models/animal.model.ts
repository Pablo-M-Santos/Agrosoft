export interface Animal {
  id: string;
  name: string;
  weight: number;
  entryDate?: string;

  status: string;

  animalTypeId: string;
  animalTypeName: string;

  responsibleEmployeeId?: string;
  responsibleEmployeeName?: string;

  statusText?: string;
  statusColor?: string;
  statusTextColor?: string;
}
