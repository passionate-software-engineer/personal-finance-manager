import {DateHelper} from './date-helper';

describe('Should compare 2 dates', function () {

  it('should return 1 when first date is after second date', () => {
    const date1 = DateHelper.getDate(3);
    const date2 = DateHelper.getDate(2);

    expect(DateHelper.compareDates(date1, date2))
    .toBe(1);
  });

  it('should return 1 when first date is after second date when dates include current time (HH:MM)', () => {
    const date1 = new Date(2019, 12, 1, 12, 58);
    const date2 = new Date(2019, 12, 1, 12, 57);

    expect(DateHelper.compareDates(date1, date2))
    .toBe(1);
  });

  it('should return -1 when first date is before second date', () => {
    const date1 = DateHelper.getDate(1);
    const date2 = DateHelper.getDate(2);

    expect(DateHelper.compareDates(date1, date2))
    .toBe(-1);
  });

  it('should return -1 when first date is before second date when dates include current time (HH:MM)', () => {
    const date1 = new Date(2019, 12, 1, 12, 58);
    const date2 = new Date(2019, 12, 1, 12, 59);

    expect(DateHelper.compareDates(date1, date2))
    .toBe(-1);
  });

  it('should return -1 when first date (YYYY,MM,DD HH:MM) is before second date (YYYY,MM,DD)', () => {
    const date1 = new Date(2019, 12, 1, 12, 59);
    const date2 = new Date(2019, 12, 2);

    expect(DateHelper.compareDates(date1, date2))
    .toBe(-1);
  });

  it('should return 0 for same dates', () => {
    const date1 = DateHelper.getDate(1);
    const date2 = DateHelper.getDate(1);

    expect(DateHelper.compareDates(date1, date2))
    .toBe(0);
  });

  it('should return 0 for same dates in different formats', () => {
    const date1 = new Date(2019, 12, 1, 0, 0, 0, 0);
    const date2 = new Date(2019, 12, 1);

    expect(DateHelper.compareDates(date1, date2))
    .toBe(0);
  });
});
