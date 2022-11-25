package brainwind.letstalksample.data.memory;

import android.content.Context;
import android.content.SharedPreferences;

import brainwind.letstalksample.R;



public class Memory
{

    Context context;
    public Memory() {

    }
    public Memory(Context context) {
        this.context = context;
    }

    public void Save(Context context, String key, String value)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getResources().getString(R.string.preference_file_key)
                , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public void Save(String key, String value)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getResources().getString(R.string.preference_file_key)
                , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void Save(Context context,String key,int value)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getResources().getString(R.string.preference_file_key)
                , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void Save(String key,int value)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getResources().getString(R.string.preference_file_key)
                , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }



    public void Save(Context context,String key,boolean value)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getResources().getString(R.string.preference_file_key)
                , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
    public void Save(String key,boolean value)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getResources().getString(R.string.preference_file_key)
                , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public String getString(String key)
    {

        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getResources().getString(R.string.preference_file_key)
                , Context.MODE_PRIVATE);
        String km = sharedPref.getString(key, "");
        return km;
    }
    public int getInteger(String key)
    {

        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getResources().getString(R.string.preference_file_key)
                , Context.MODE_PRIVATE);
        int km = sharedPref.getInt(key, -1);
        return km;
    }
    public boolean getBoolean(String key)
    {

        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getResources().getString(R.string.preference_file_key)
                , Context.MODE_PRIVATE);
        boolean km = sharedPref.getBoolean(key, false);
        return km;
    }


}
