export class DateHelper {

   static DayInMs = 86_400_000;

  public static isFutureDate(date: Date): boolean {
    return new Date(date).getTime() > Date.now();
  }

  public static isPastDate(date: Date): boolean {
    return new Date(date).getTime() + this.DayInMs < Date.now();
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

 public static getDate = currentDatePlusDays => new Date((new Date()).getTime() + (currentDatePlusDays * DateHelper.DayInMs));

}
