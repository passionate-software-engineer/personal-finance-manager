export class DateHelper {

  static dayDurationInMs = 86_400_000;

  public static isFutureDate(date: Date): boolean {
    return new Date(date).getTime() > Date.now();
  }

  public static isPastDate(date: Date): boolean {
    return new Date(date).getTime() + this.dayDurationInMs < Date.now();
  }

  public static compareDates(date1: Date, date2: Date) {
    if (date1.getTime() < date2.getTime()) {
      return -1;
    }
    if (date1.getTime() > date2.getTime()) {
      return 1;
    }
    return 0;
  }

  public static getDate(currentDatePlusDays) {
    const currentTimeInMs = new Date().getTime();
    return new Date(currentTimeInMs + (currentDatePlusDays * DateHelper.dayDurationInMs));
  }

}
