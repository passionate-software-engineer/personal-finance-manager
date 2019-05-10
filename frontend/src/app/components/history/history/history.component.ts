import {Component, OnInit} from '@angular/core';
import {History} from '../history';
import {HistoryService} from '../history-service/history.service';
import {AlertsService} from '../../alert/alerts-service/alerts.service';
import {Sortable} from '../../../helpers/sortable';
import {HistoryAccountPriceEntry} from '../historyInfo';

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})

export class HistoryComponent extends Sortable implements OnInit {
  history: History[] = [];

  constructor(private historyService: HistoryService, private alertService: AlertsService) {
    super('date', true);
  }

  ngOnInit() {
    this.getHistory();
  }

  getHistory(): void {
    this.historyService.getHistory()
        .subscribe(history => {
          this.history = history;
          for (const historyEntry of history) {
            if ((historyEntry.type !== 'ADD' && historyEntry.type !== 'UPDATE') || historyEntry.object !== 'Transaction') {
              continue;
            }

            for (const entry of historyEntry.entries) {
              if (entry.name !== 'accountPriceEntries') {
                continue;
              }

              entry.newAccountPriceEntries = [];
              entry.newValue = entry.newValue.replace('[', '{\"');
              entry.newValue = entry.newValue.replace(']', '}');
              entry.newValue = entry.newValue.replace(/ : /g, '\" : ');
              entry.newValue = entry.newValue.replace(/, /g, ', \"');
              const parsedNewValue = this.objToStrMap(JSON.parse(entry.newValue));

              parsedNewValue.forEach((value: number, key: string) => {
                const accountPriceEntry = new HistoryAccountPriceEntry();
                accountPriceEntry.accountName = key;
                accountPriceEntry.amount = value;
                entry.newAccountPriceEntries.push(accountPriceEntry);
              });

              // TODO - simplify - no need to convert to map first
            }
          }

          // TODO - remove duplicated code
          for (const historyEntry of history) {
            if ((historyEntry.type !== 'DELETE' && historyEntry.type !== 'UPDATE') || historyEntry.object !== 'Transaction') {
              continue;
            }

            for (const entry of historyEntry.entries) {
              if (entry.name !== 'accountPriceEntries') {
                continue;
              }

              entry.oldAccountPriceEntries = [];
              entry.oldValue = entry.oldValue.replace('[', '{\"');
              entry.oldValue = entry.oldValue.replace(']', '}');
              entry.oldValue = entry.oldValue.replace(/ : /g, '\" : ');
              entry.oldValue = entry.oldValue.replace(/, /g, ', \"');
              const parsedNewValue = this.objToStrMap(JSON.parse(entry.oldValue));

              parsedNewValue.forEach((value: number, key: string) => {
                const accountPriceEntry = new HistoryAccountPriceEntry();
                accountPriceEntry.accountName = key;
                accountPriceEntry.amount = value;
                entry.oldAccountPriceEntries.push(accountPriceEntry);
              });

              // TODO - simplify - no need to convert to map first
            }
          }

        });
  }

  objToStrMap(obj) {
    const strMap = new Map();
    for (const k of Object.keys(obj)) {
      strMap.set(k, obj[k]);
    }
    return strMap;
  }

  onRefreshHistory() {
    this.getHistory();
  }
}
