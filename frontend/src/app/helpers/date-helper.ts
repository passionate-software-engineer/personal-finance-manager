export class DateHelper {

  public static isFutureDate(date: Date): boolean {
    const oneDayInMiliseconds = 86400000;
    return date.getTime() - oneDayInMiliseconds > Date.now();
  }

  public static isPastDate(date: Date): boolean {
    return new Date(date).getTime() < Date.now();
  }
}
