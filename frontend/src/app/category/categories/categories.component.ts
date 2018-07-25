import {Component, OnInit} from '@angular/core';
import {Category} from '../category';
import {CategoryService} from '../category-service/category.service';
import {MessagesService} from '../../messages/messages.service';
import {catchError, map, tap} from 'rxjs/operators';
import {isNumber} from 'util';
import {isNumeric} from 'rxjs/internal-compatibility';
import {AlertsService} from '../../alerts/alerts-service/alerts.service';

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
  newCategoryName: string;
  selectedCategory: Category;
  editedParentCategory: Category = new Category();
  id;
  sthGoesWrong = 'Something goes wrong ,try again';

  constructor(private categoryService: CategoryService, private alertService: AlertsService) {
  }


  ngOnInit() {
    this.getCategories();

  }

  getCategories(): void {
    this.categoryService.getCategories()
      .subscribe(categories => {
          this.categories = categories;
        }, () => {
          this.alertService.error(this.sthGoesWrong);
        }
      );
  }

  deleteCategory(category) {
    this.categoryService.deleteCategory(category.id).subscribe(
      () => {
        this.alertService.info('Category deleted');
      },
      error1 => {
        this.alertService.error(this.sthGoesWrong);
      }
    );
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
    if (!this.validateCategory(this.editedName)) {
      return;
    }
    category.name = this.editedName;
    category.parentCategory = this.editedParentCategory;
    this.categoryService.editCategory(category).subscribe(
      () => {
        this.alertService.success('Category edited');
        category.editMode = false;
        this.editedParentCategory = null;
        this.editedName = null;
      }, () => {
        this.alertService.error(this.sthGoesWrong);
      }
    );
  }

  onAddCategory() {
    this.categoryToAdd = new Category();
    if (!this.validateCategory(this.newCategoryName)) {
      return;
    }
    this.categoryToAdd.name = this.newCategoryName;
    this.categoryToAdd.parentCategory = this.selectedCategory;
    this.categoryService.addCategory(this.categoryToAdd)
      .subscribe(id => {
          this.categoryToAdd.id = id;
          this.categories.push(this.categoryToAdd);
          this.alertService.success('Category added');
          this.addingMode = false;
          this.newCategoryName = null;
        }
        , () => {
          this.alertService.error(this.sthGoesWrong);
        });
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

  validateCategory(categoryName: string): boolean {
    if (categoryName == null || categoryName === '') {
      this.alertService.error('Category name cannot be empty');
      return false;
    }
    return true;
  }
}
