export class DateHelper {

  public static isFutureDate(date: Date): boolean {
    return new Date(date).getTime() > Date.now();
  }

  public static isPastDate(date: Date): boolean {
    const ONE_DAY_IN_MS = 86_400_000;
    return new Date(date).getTime() + ONE_DAY_IN_MS < Date.now();
  }
}
