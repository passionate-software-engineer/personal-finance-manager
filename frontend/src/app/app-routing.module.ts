import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CategoriesComponent } from './category/categories/categories.component';
import { AccountsListComponent } from './account/accounts-list/accounts-list.component';
import {TransactionsComponent} from './transaction/transactions/transactions.component';

const routes: Routes = [
  { path: '', redirectTo: '/transactions', pathMatch: 'full' },
  { path: 'categories', component: CategoriesComponent },
  { path: 'accounts', component: AccountsListComponent },
  { path: 'transactions', component: TransactionsComponent }
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
