import {Component, OnInit} from '@angular/core';
import {Category} from '../category';
import {CategoryService} from '../category-service/category.service';
import {MessagesService} from '../../messages/messages.service';
import {catchError, map, tap} from 'rxjs/operators';
import {isNumber} from 'util';

@Component({
  selector: 'app-categories',
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.css']
})
export class CategoriesComponent implements OnInit {
  categories: Category[];
  possibleParentCategories: Category[];
  categoryToAdd: Category = new Category();
  addingMode = false;
  editedName: string;
  selectedCategory: Category;
  editedParentCategory: Category = new Category();
  id;

  constructor(private categoryService: CategoryService) {
  }

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

  onShowEditMode(category: Category) {
    category.editMode = true;
    this.editedName = category.name;
    this.editedParentCategory = category.parentCategory;
    this.refreshListOfPossibleParentCategories(category);
  }

  onEditCategory(category: Category) {
    category.name = this.editedName;
    category.parentCategory = this.editedParentCategory;
    this.categoryService.editCategory(category).subscribe();
    category.editMode = false;
  }

  onAddCategory(nameInput: HTMLInputElement) {
    this.categoryToAdd = new Category();
    this.categoryToAdd.name = nameInput.value;
    this.categoryToAdd.parentCategory = this.selectedCategory;
    this.categoryService.addCategory(this.categoryToAdd)
      .subscribe(id => {
        if (id.isNumber()) {
          this.categoryToAdd.id = id;
          this.categories.push(this.categoryToAdd);
        }
      });
    this.addingMode = false;
  }

  onRefreshCategories() {
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

  sortByParentCategory(type: string) {
    if (type === 'asc') {
      this.categories.sort((a1, a2) => {
        if (a1.parentCategory == null) {
          return 1;
        }
        if (a2.parentCategory == null) {
          return -1;
        }
        return a1.parentCategory.name.toLowerCase() > a2.parentCategory.name.toLowerCase() ? -1 : 1;
      });
    }
    if (type === 'dsc') {
      this.categories.sort((a1, a2) => {
        if (a1.parentCategory == null) {
          return -1;
        }
        if (a2.parentCategory == null) {
          return 1;
        }
        return a1.parentCategory.name.toLowerCase() < a2.parentCategory.name.toLowerCase() ? -1 : 1;
      });
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

  getParentCategoryName(category): string {
    if (category.parentCategory != null) {
      return category.parentCategory.name;
    }
    return 'Main Category';
  }

  // add sort by ParentCategory

  refreshListOfPossibleParentCategories(cat: Category) {
    this.possibleParentCategories = this.categories
      .filter(element => element.id !== cat.id)
      .filter(x => {
        if (x.parentCategory == null) {
          return true;
        } else {
          return x.parentCategory.id !== cat.id;
        }
      });
  }
}
