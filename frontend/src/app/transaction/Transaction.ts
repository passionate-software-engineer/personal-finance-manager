export class Transaction {
  id: number;
  name: string;
  parentCategory: Transaction = null;
  editMode = false;
  editedName: string;
  editedParentCategory: Transaction;
}
