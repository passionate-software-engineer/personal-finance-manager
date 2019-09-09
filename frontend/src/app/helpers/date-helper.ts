export class DateHelper {

  public isFutureDate(date: Date): boolean {
    date = this.getCorrectDate(date);
    const oneDayInMiliseconds = 86400000;
    return date.getTime() - oneDayInMiliseconds > Date.now();
  }

  public isPastDate(date: Date): boolean {
    return new Date(date).getTime() < Date.now();
  }

  getCorrectDate(date: Date): Date {
    const TWO_HOURS = 120;
    date.setMinutes(date.getMinutes() + TWO_HOURS);
    return date;
  }
}
