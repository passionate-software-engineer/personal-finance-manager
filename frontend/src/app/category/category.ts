export class Category {
  id: number;
  name: string;
  parentCategory: Category = null;
  editMode = false;
  editedName: string; // TODO use object as in Transactions
  editedParentCategory: Category;
}
