package brainwind.letstalksample.forms.general;

import java.util.Calendar;

public class DateRangeForActivity
{

    Calendar now = Calendar.getInstance();
    int year=now.get(Calendar.YEAR);
    int month=now.get(Calendar.MONTH);
    int day=now.get(Calendar.DAY_OF_MONTH);

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }


}
