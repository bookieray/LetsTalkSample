package brainwind.letstalksample.data.utilities;

public class NumUtils
{

    public static String getAbbreviatedNum(long number)
    {

        String h="";
        long b=1000000000;
        long m=1000000;
        long thousand=1000;
        long ten_thousand=10000;

        long ten_m=10000000;
        //
        long rem=number%b;

        if(number>=b)
        {
            if(rem>=ten_m)
            {
                long jnm=rem/ten_m;
                if(jnm>=10)
                {
                    jnm=jnm/10;
                }
                long jn=number/b;
                h=jn+"."+jnm+"B";
            }
            else
            {
                h=(number/b)+"B";
            }
        }
        else if(number>=m)
        {
            rem=number%m;
            if(rem>=ten_thousand)
            {
                long jnm=rem/ten_thousand;
                if(jnm>=10)
                {
                    jnm=jnm/10;
                }
                long jn=number/m;
                h=jn+"."+jnm+"M";
            }
            else
            {
                h=(number/m)+"M";
            }
        }
        else if(number>=thousand)
        {
            rem=number%thousand;
            if(rem>=10)
            {
                long jnm=rem/10;
                if(jnm>=10)
                {
                    jnm=jnm/10;
                }
                long jn=number/thousand;
                h=jn+"."+jnm+"K";
            }
            else
            {
                h=(number/thousand)+"K";
            }

        }
        else
        {
            h=number+"";
        }

        return h;

    }

}
