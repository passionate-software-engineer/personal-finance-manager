import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppComponent} from './components/app/app.component';
import {AccountsComponent} from './components/account/accounts/accounts.component';
import {HTTP_INTERCEPTORS, HttpClient, HttpClientModule} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CategoriesComponent} from './components/category/categories/categories.component';
import {NavigationComponent} from './components/navigation/navigation.component';
import {AlertsComponent} from './components/alert/alerts/alerts.component';
import {AlertsService} from './components/alert/alerts-service/alerts.service';
import {TransactionsComponent} from './components/transaction/transactions/transactions.component';
import {HistoryComponent} from './components/history/history/history.component';
import {OrderModule} from 'ngx-order-pipe';
import {routing} from './app-routing.module';
import {AuthenticationService} from './authentication/authentication.service';
import {UserService} from './authentication/user.service';
import {AuthGuard} from './authentication/auth.guard';
import {HeadersInterceptor} from './interceptors/headers-interceptor.service';
import {ErrorInterceptor} from './interceptors/error.interceptor';
import {LoginComponent} from './components/login/login.component';
import {RegisterComponent} from './components/register/register.component';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {ImportExportComponent} from './components/import-export/import-export/import-export.component';
import {PlannedTransactionsComponent} from './planned-transaction/planned-transactions.component';

export const createTranslateLoader = (http: HttpClient) => {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
};

@NgModule({
  declarations: [
    AppComponent,
    AccountsComponent,
    CategoriesComponent,
    TransactionsComponent,
    NavigationComponent,
    AlertsComponent,
    LoginComponent,
    RegisterComponent,
    HistoryComponent,
    ImportExportComponent,
    PlannedTransactionsComponent
  ],
  imports: [
    BrowserModule,
    ReactiveFormsModule,
    FormsModule,
    HttpClientModule,
    routing,
    OrderModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: createTranslateLoader,
        deps: [HttpClient]
      }
    })
  ],
  providers: [
    AuthGuard,
    AlertsService,
    AuthenticationService,
    UserService,
    {provide: HTTP_INTERCEPTORS, useClass: HeadersInterceptor, multi: true},
    {provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true}
  ],
  bootstrap: [
    AppComponent
  ]
})
export class AppModule {
}
