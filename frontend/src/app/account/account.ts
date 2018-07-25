export class Account {
  id: number;
  name: string;
  balance: number;
  editMode = false;
  editedName: string = this.name;
  editedBalance: number = this.balance;
}
