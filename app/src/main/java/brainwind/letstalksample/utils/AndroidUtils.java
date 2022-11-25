package brainwind.letstalksample.utils;

import android.content.Context;

public class AndroidUtils
{

    //private var density = 1f
    private static float density = 1f;

    /*
    fun dp(value: Float, context: Context): Int {
        if (density == 1f) {
            checkDisplaySize(context)
        }
        return if (value == 0f) {
            0
        } else Math.ceil((density * value).toDouble()).toInt()
    }
     */

    //fun dp(value: Float, context: Context): Int
    public static int dp(Float value, Context context)
    {

        if (density == 1f)
        {
            checkDisplaySize(context);
        }
        if (value == 0f) {
        return 0;
        }
        else return (int) Math.ceil(Double.valueOf(density * value));

    }

    private static void checkDisplaySize(Context context)
    {

        try
        {
            //density = context.resources.displayMetrics.density
            density = context.getResources().getDisplayMetrics().density;
        }
        catch (Exception e)
        {

        }

    }


}
