import {Transaction} from '../transaction';

export class CommitBodyResponse {

  committed: Transaction;
  scheduledForNextMonth: Transaction;
}
