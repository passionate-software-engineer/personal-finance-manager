export class Sortable {

  public order: string;
  public reverse = false;

  constructor(order: string) {
    this.order = order;
  }

  public setOrder(value: string) {
    if (this.order === value) {
      this.reverse = !this.reverse;
    }
    this.order = value;
  }
}
