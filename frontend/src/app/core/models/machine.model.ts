export interface Machine {
  id: string;
  name: string;
  type: string;
  brand: string;
  model: string;
  serialNumber?: string;
  manufacturingYear?: number;
  purchaseValue?: number;
  purchaseDate?: string;
  createdAt?: string;
  status: string;
  assignedEmployeeId?: string;
  assignedEmployeeName?: string;
  assignedEmployee?: {
    id: string;
    fullName: string;
  };
  statusText?: string;
  statusClass?: string;
  statusColor?: string;
  statusTextColor?: string;
}
