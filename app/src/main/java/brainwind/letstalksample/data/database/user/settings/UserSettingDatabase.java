package brainwind.letstalksample.data.database.user.settings;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = {UserSetting.class}, version = 1)
public abstract class UserSettingDatabase extends RoomDatabase
{

    public static String DB_NAME="user_settings_db";
    public abstract UserSettingDao userSettingDao();
    public static UserSettingDatabase instance;

    private static volatile UserSettingDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 1;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static UserSettingDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (UserSettingDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    UserSettingDatabase.class, DB_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }


}
