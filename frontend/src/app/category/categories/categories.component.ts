import {Component, OnInit} from '@angular/core';
import {Category} from '../category';
import {CategoryService} from '../category-service/category.service';
import {AlertsService} from '../../alerts/alerts-service/alerts.service';

@Component({
  selector: 'app-categories',
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.css']
})
export class CategoriesComponent implements OnInit {
  order = 'name';
  reverse = false;
  categories: Category[];
  addingMode = false;
  newCategoryName: string;
  newCategoryParentCategory: Category = null;

  constructor(private categoryService: CategoryService, private alertService: AlertsService) {
  }

  ngOnInit() {
    this.getCategories();
  }

  getCategories(): void {
    this.categoryService.getCategories()
      .subscribe(categories => {
        this.categories = categories;
      });
  }

  // TODO make nice looking confirmation popup

  deleteCategory(category) {
    if (confirm('Are you sure You want to delete this account ?')) {
      this.categoryService.deleteCategory(category.id)
        .subscribe(() => {
          this.alertService.success('Category deleted');
          const index: number = this.categories.indexOf(category);
          if (index !== -1) {
            this.categories.splice(index, 1);
          }
        });
    }
  }

  onShowEditMode(category: Category) {
    category.editMode = true;
    category.editedName = category.name;
    if (category.parentCategory == null) {
      category.editedParentCategory = null;
    } else {
      category.editedParentCategory = category.parentCategory;
    }

    for (const categoryEntry of this.categories) {
      if (categoryEntry.id === category.editedParentCategory.id) {
        category.editedParentCategory = categoryEntry;
      }
    }

  }

  onEditCategory(category: Category) {
    if (!this.validateCategory(category.editedName)) {
      return;
    }

    const editedCategory: Category = new Category();
    editedCategory.id = category.id;
    editedCategory.name = category.editedName;
    editedCategory.parentCategory = category.editedParentCategory;
    this.categoryService.editCategory(editedCategory)
      .subscribe(() => {
        this.alertService.success('Category edited');
        Object.assign(category, editedCategory);
      });
  }

  onAddCategory() {
    const categoryToAdd = new Category();
    if (!this.validateAddingCategory(this.newCategoryName)) {
      return;
    }
    categoryToAdd.name = this.newCategoryName;
    categoryToAdd.parentCategory = this.newCategoryParentCategory;
    this.categoryService.addCategory(categoryToAdd)
      .subscribe(id => {
        categoryToAdd.id = id;
        this.categories.push(categoryToAdd);
        this.alertService.success('Category added');
        this.addingMode = false;
        this.newCategoryName = null;
        this.newCategoryParentCategory = null;
      });
  }

  onRefreshCategories() {
    this.getCategories();
  }

  getParentCategoryName(category): string {
    if (category.parentCategory != null) {
      return category.parentCategory.name;
    }
    return 'Main Category';
  }

  getListOfPossibleParentCategories(cat: Category) {
    return this.categories.filter(category => {
      if (category.id === cat.id) {
        return false;
      }
      let categoryToCheck = category.parentCategory;
      while (categoryToCheck != null) {

        if (categoryToCheck.id === cat.id) {
          return false;
        }
        categoryToCheck = categoryToCheck.parentCategory;
      }
      return true;
    });
  }

  validateCategory(categoryName: string): boolean {
    if (categoryName == null || categoryName.trim() === '') {
      this.alertService.error('Category name cannot be empty');
      return false;
    }
    if (categoryName.length > 70) {
      this.alertService.error('Category name too long. Category name can not be longer then 100 characters');
      return false;
    }
    return true;
  }

  validateAddingCategory(categoryName: string): boolean {
    if (!this.validateCategory(categoryName)) {
      return false;
    }

    if (this.categories.filter(category =>
        category.name.toLowerCase() === categoryName.toLowerCase()).length > 0) {
      this.alertService.error('Category with provided name already exist');
      return false;
    }
    return true;
  }

  setOrder(value: string) {
    if (this.order === value) {
      this.reverse = !this.reverse;
    }
    this.order = value;
  }
}
