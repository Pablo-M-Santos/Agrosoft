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
  status: string;
  assignedEmployeeId?: string;
  assignedEmployeeName?: string;
  statusText?: string;
  statusClass?: string;
}
