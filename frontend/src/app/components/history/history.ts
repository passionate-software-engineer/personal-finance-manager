import {HistoryInfo} from './historyInfo';

export class History {
  id: number;
  date: string;
  type: string;
  object: string;
  entries: HistoryInfo[];
  editMode = false;
}
