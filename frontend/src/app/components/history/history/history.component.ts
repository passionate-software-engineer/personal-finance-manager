import {Component, OnInit} from '@angular/core';
import {History} from '../history';
import {HistoryService} from '../history-service/history.service';
import {isNumeric} from 'rxjs/internal-compatibility';
import {AlertsService} from '../../alert/alerts-service/alerts.service';
import {Sortable} from '../../../helpers/sortable';

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})

export class HistoryComponent extends Sortable implements OnInit {
  history: History[] = [];
  addingMode = false;
  newHistory: History = new History();

  constructor(private historyService: HistoryService, private alertService: AlertsService) {
    super('name');
  }

  ngOnInit() {
    this.getHistory();
  }

  getHistory(): void {
    this.historyService.getHistory()
      .subscribe(history => {
        this.history = history;
      });
  }

  onRefreshHistory() {
    this.getHistory();
  }
}
