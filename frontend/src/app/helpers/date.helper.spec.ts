import {DateHelper} from './date.helper';

describe('Should compare 2 dates', function () {

  it('should return 1 when first date is after second date', () => {
    const date1 = DateHelper.getDate(3);
    const date2 = DateHelper.getDate(2);

    expect(DateHelper.compareDates(date1, date2)).toBe(1);
  });

  it('should return -1 when first date is before second date', () => {
    const date1 = DateHelper.getDate(1);
    const date2 = DateHelper.getDate(2);

    expect(DateHelper.compareDates(date1, date2)).toBe(-1);
  });

  it('should return 0 for same dates', () => {
    const date1 = DateHelper.getDate(1);
    const date2 = DateHelper.getDate(1);

    expect(DateHelper.compareDates(date1, date2)).toBe(0);
  });
});
