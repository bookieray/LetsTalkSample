package brainwind.letstalksample.data.database.user.settings;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserSettingDao
{


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(UserSetting... userSettings);

    @Delete
    void delete(UserSetting userSettings);

    @Query("DELETE FROM UserSetting")
    void deleteAll();

    @Update
    public void updateOrgs(UserSetting... userSettings);

    @Query("SELECT * FROM UserSetting where uid=:uid order by setting_name asc")
    List<UserSetting> getAll(String uid);

    @Query("SELECT count(*) FROM UserSetting where uid=:uid order by uid asc")
    int countUserSettings(String uid);

    @Query("SELECT * FROM UserSetting where uid=:uid order by setting_name asc limit :limit")
    List<UserSetting> getUserSettings(String uid, int limit);

    @Query("SELECT * FROM UserSetting where uid=:uid and setting_name=:setting_name limit 1")
    UserSetting getUserSetting(String uid,String setting_name);


}
