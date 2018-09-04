import {Component, OnInit} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css']
})
export class NavigationComponent implements OnInit {

constructor(public translate: TranslateService) {
    translate.setDefaultLang('en');
  }

  switchLanguage = (lang: string) => {
    this.translate.use(lang);
  }

  ngOnInit() {
  }

}
