import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { AccountsListComponent } from './account/accounts-list/accounts-list.component';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CategoriesComponent } from './category/categories/categories.component';
import { AppRoutingModule } from './/app-routing.module';
import { NavigatiorComponent } from './navigation/navigatior/navigatior.component';
import { NavigationComponent } from './navigation/navigation/navigation.component';

@NgModule({
  declarations: [
    AppComponent,
    AccountsListComponent,
    CategoriesComponent,
    NavigatiorComponent,
    NavigationComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
