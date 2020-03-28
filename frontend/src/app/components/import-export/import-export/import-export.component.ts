import {Component} from '@angular/core';
import {ImportExportService} from '../history-service/import-export.service';
import {AlertsService} from '../../alert/alerts-service/alerts.service';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-import-export',
  templateUrl: './import-export.component.html',
  styleUrls: ['./import-export.component.scss']
})

export class ImportExportComponent {

  exportContent: string;

  constructor(
    private importExportService: ImportExportService,
    private alertService: AlertsService,
    private translate: TranslateService
  ) {
  }

  callExportService(): void {
    this.importExportService.getExportContent()
        .subscribe(exportContent => {
          this.alertService.success(this.translate.instant('message.dataExportedSuccessfully'));
          this.exportContent = JSON.stringify(exportContent, null, 2);
        });
  }

  callImportService(): void {
    if (this.exportContent == null || this.exportContent === '') {
      this.alertService.error(this.translate.instant('import.noContentProvided'));
      return;
    }

    this.importExportService.importContent(this.exportContent)
        .subscribe(() => {
          this.alertService.success(this.translate.instant('message.dataImportedSuccessfully'));
        });
  }

}
