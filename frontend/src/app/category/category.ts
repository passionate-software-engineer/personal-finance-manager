export class Category {
  id: number;
  name: string;
  parentCategory: Category = null;
  editMode = false;
  editedCategory: Category;
}
