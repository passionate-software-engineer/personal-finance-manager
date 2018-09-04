import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppComponent} from './core/app.component';
import {AccountsListComponent} from './account/accounts-list/accounts-list.component';
import {HttpClientModule, HttpClient} from '@angular/common/http';
import {FormsModule} from '@angular/forms';
import {CategoriesComponent} from './category/categories/categories.component';
import {AppRoutingModule} from './app-routing.module';
import {NavigationComponent} from './navigation/navigation/navigation.component';
import {AlertsComponent} from './alerts/alerts.component';
import {AlertsService} from './alerts/alerts-service/alerts.service';
import {TransactionsComponent} from './transaction/transactions/transactions.component';
import {OrderModule} from 'ngx-order-pipe';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

export const createTranslateLoader = (http: HttpClient) => {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
};

@NgModule({
  declarations: [
    AppComponent,
    AccountsListComponent,
    CategoriesComponent,
    TransactionsComponent,
    NavigationComponent,
    AlertsComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    AppRoutingModule,
    OrderModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: createTranslateLoader,
        deps: [HttpClient]
      }
    })
  ],
  providers: [AlertsService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
