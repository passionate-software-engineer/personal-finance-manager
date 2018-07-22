import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {AccountsListComponent} from './account/accounts-list/accounts-list.component';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule} from '@angular/forms';
import {CategoriesComponent} from './category/categories/categories.component';
import {AppRoutingModule} from './/app-routing.module';
import {NavigationComponent} from './navigation/navigation/navigation.component';
import {MessagesComponent} from './messages/messages/messages.component';
import {AlertsComponent} from './alerts/alerts.component';
import {AlertsService} from './alerts/alerts-service/alerts.service';


@NgModule({
  declarations: [
    AppComponent,
    AccountsListComponent,
    CategoriesComponent,
    NavigationComponent,
    MessagesComponent,
    AlertsComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [AlertsService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
