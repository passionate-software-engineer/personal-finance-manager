import { Component, OnInit } from '@angular/core';
import { Category } from '../category';
import { CategoryService } from '../category-service/category.service';

@Component({
  selector: 'app-categories',
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.css']
})
export class CategoriesComponent implements OnInit {
  categories:Category[];
  categoryToAdd: Category;
  addingMode = false;
  editedName: string;
  editedParentCategory: number;
  id;

  constructor(private categoryService: CategoryService) { }

  ngOnInit() {
    this.getCategories();
  }

  getCategories(): void {
    this.categoryService.getCategories()
      .subscribe(categories => {
        if (categories === null) {
          this.categories = [];
        } else {
          this.categories = categories;
        }
      });
  }

  deleteCategory(category) {
    this.categoryService.deleteCategory(category.id).subscribe();
    const index: number = this.categories.indexOf(category);
    if (index !== -1) {
      this.categories.splice(index, 1);
    }
  }

  onShowEditMode(account: Account) {
   
  }

  onEditCategory(account: Account) {
    
  }

  onAddCategory(nameInput: HTMLInputElement, parentCategory: Category) {
    this.categoryToAdd = new Category();
    this.categoryToAdd.name = nameInput.value;
    this.categoryToAdd.parentCategory = parentCategory;
    this.categoryService.addCategory(this.categoryToAdd)
      .subscribe(id => {
        this.categoryToAdd.id = id;
        this.categories.push(this.categoryToAdd);
      });
    this.addingMode = false;
  }

  onRefreshAccounts() {
    this.getCategories();
  }

  sortByName(type: string) {
    if (type === 'asc') {
      this.categories.sort((a1, a2) => (a1.name.toLowerCase() > a2.name.toLowerCase() ? -1 : 1));
    }
    if (type === 'dsc') {
      this.categories.sort((a1, a2) => (a1.name.toLowerCase() > a2.name.toLowerCase() ? 1 : -1));
    }
  }

  sortById(sortingType: string) {
    if (sortingType === 'asc') {
      this.categories.sort((a1, a2) => a1.id - a2.id);
    }
    if (sortingType === 'dsc') {
      this.categories.sort((a1, a2) => a2.id - a1.id);
    }
  }
}
