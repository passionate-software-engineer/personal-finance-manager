import {RouterModule, Routes} from '@angular/router';
import {CategoriesComponent} from './category/categories/categories.component';
import {AccountsListComponent} from './account/accounts-list/accounts-list.component';
import {TransactionsComponent} from './transaction/transactions/transactions.component';
import {AuthGuard} from './_guards/auth.guard';
import {LoginComponent} from './login/login.component';
import {RegisterComponent} from './register/register.component';

const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'categories', component: CategoriesComponent, canActivate: [AuthGuard]},
  {path: 'accounts', component: AccountsListComponent, canActivate: [AuthGuard]},
  {path: 'transactions', component: TransactionsComponent, canActivate: [AuthGuard]},

  // otherwise redirect to home
  {path: '**', redirectTo: '/transactions', canActivate: [AuthGuard]}
];

export const routing = RouterModule.forRoot(routes);
