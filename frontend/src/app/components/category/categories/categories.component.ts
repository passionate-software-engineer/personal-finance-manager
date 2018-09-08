import {Component, OnInit} from '@angular/core';
import {Category} from '../category';
import {CategoryService} from '../category-service/category.service';
import {AlertsService} from '../../alert/alerts-service/alerts.service';
import {Sortable} from '../../../helpers/sortable';

@Component({ // TODO categories in dropdows should display with parent category e.g. Car > Parts (try using filter for it)
  selector: 'app-categories',
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.css']
})
export class CategoriesComponent extends Sortable implements OnInit {
  categories: Category[] = [];
  addingMode = false;
  newCategory: Category = new Category();

  constructor(private categoryService: CategoryService, private alertService: AlertsService) {
    super('name');
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
    category.editedCategory = new Category();
    category.editedCategory.id = category.id;
    category.editedCategory.name = category.name;

    if (category.parentCategory != null) {
      category.editedCategory.parentCategory = category.parentCategory;

      // TODO that should not be needed if value in category is set correctly
      for (const categoryEntry of this.categories) {
        if (categoryEntry.id === category.editedCategory.parentCategory.id) {
          category.editedCategory.parentCategory = categoryEntry;
        }
      }
    }
  }

  onEditCategory(category: Category) {
    if (!this.validateCategory(category.editedCategory.name)) {
      return;
    }

    this.categoryService.editCategory(category.editedCategory)
      .subscribe(() => {
        this.alertService.success('Category edited');
        Object.assign(category, category.editedCategory);
        category.editedCategory = new Category();
        // TODO get category from server
      });
  }

  onAddCategory() {
    if (!this.validateAddingCategory(this.newCategory.name)) {
      return;
    }

    this.categoryService.addCategory(this.newCategory)
      .subscribe(id => {
        this.newCategory.id = id;
        this.categories.push(this.newCategory);
        this.newCategory = new Category();
        this.alertService.success('Category added');
        this.addingMode = false;

        // TODO get category from server
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

  validateCategory(categoryName: string): boolean { // TODO pass category object
    if (categoryName == null || categoryName.trim() === '') {
      this.alertService.error('Category name cannot be empty');
      return false; // TODO validate all - not break on first failure
    }
    if (categoryName.length > 100) {
      this.alertService.error('Category name too long. Category name can not be longer then 100 characters');
      return false;
    }
    return true;
  }

  validateAddingCategory(categoryName: string): boolean {
    if (!this.validateCategory(categoryName)) {
      return false;
    }

    if (this.categories.filter(category => category.name.toLowerCase() === categoryName.toLowerCase()).length > 0) {
      this.alertService.error('Category with provided name already exist');
      return false;
    }
    return true;
  }
}
