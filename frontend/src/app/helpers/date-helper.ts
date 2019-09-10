export class DateHelper {

  public static isFutureDate(date: Date): boolean {
    return date.getTime()  > Date.now();
  }

  public static isPastDate(date: Date): boolean {
    return new Date(date).getTime() < Date.now();
  }
}
