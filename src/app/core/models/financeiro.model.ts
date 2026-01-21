export interface Financial {
  id: string;
  description: string;
  amount: number | string;
  type: 'REVENUE' | 'EXPENSE' | 'REVENUES';
  category: string;
  transactionDate: string;
  typeText?: string;
  statusColor?: string;
  statusTextColor?: string;
}
