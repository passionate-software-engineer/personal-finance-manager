export abstract class Sortable {
  private order: string;
  private reverse = false;

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
