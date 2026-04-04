export interface Financial {
  id: string;
  description: string;
  amount: number | string;
  type: 'REVENUE' | 'EXPENSE' | 'REVENUES';
  category: string;
  transactionDate: string;
  createdAt?: string;
  updatedAt?: string;
  typeText?: string;
  typeColor?: string;
  typeTextColor?: string;
  categoryText?: string;
  categoryColor?: string;
  categoryTextColor?: string;
}
