import {RouterModule, Routes} from '@angular/router';
import {CategoriesComponent} from './components/category/categories/categories.component';
import {AccountsComponent} from './components/account/accounts/accounts.component';
import {TransactionsComponent} from './components/transaction/transactions/transactions.component';
import {AuthGuard} from './authentication/auth.guard';
import {LoginComponent} from './components/login/login.component';
import {RegisterComponent} from './components/register/register.component';
import {HistoryComponent} from './components/history/history/history.component';
import {ImportExportComponent} from './components/import-export/import-export/import-export.component';
import {PlannedTransactionsComponent} from './planned-transaction/planned-transactions.component';

const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'categories', component: CategoriesComponent, canActivate: [AuthGuard]},
  {path: 'accounts', component: AccountsComponent, canActivate: [AuthGuard]},
  {path: 'transactions', component: TransactionsComponent, canActivate: [AuthGuard]},
  {path: 'plannedTransactions', component: PlannedTransactionsComponent, canActivate: [AuthGuard]},
  {path: 'history', component: HistoryComponent, canActivate: [AuthGuard]},
  {path: 'export', component: ImportExportComponent, canActivate: [AuthGuard]},
  // otherwise redirect to home
  {path: '**', redirectTo: '/login'}

];

export const routing = RouterModule.forRoot(routes);
