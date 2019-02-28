export class Sortable {

  public order: string;
  public reverse = false;

  constructor(order: string, reverse: boolean = false) {
    this.order = order;
    this.reverse = reverse;
  }

  public setOrder(value: string) {
    if (this.order === value) {
      this.reverse = !this.reverse;
    }
    this.order = value;
  }

}
