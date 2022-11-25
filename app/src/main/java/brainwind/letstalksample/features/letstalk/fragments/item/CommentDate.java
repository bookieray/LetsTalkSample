package brainwind.letstalksample.features.letstalk.fragments.item;

import org.joda.time.LocalDateTime;

public class CommentDate
{

    private int day;
    private int month;
    private int year;

    public CommentDate(Comment comment)
    {

        if(comment.getCreatedDate()!=null)
        {

            LocalDateTime localDateTime=new LocalDateTime(comment.getCreatedDate());
            this.setDay(localDateTime.getDayOfMonth());
            this.setMonth(localDateTime.getMonthOfYear());
            this.setYear(localDateTime.getYear());

        }

    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getSignature()
    {
        return this.getDay()+"-"+this.getMonth()+"-"+this.getYear();
    }

}
