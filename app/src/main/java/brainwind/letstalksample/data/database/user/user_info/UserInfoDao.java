package brainwind.letstalksample.data.database.user.user_info;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserInfoDao
{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(UserInfo... userInfos);

    @Delete
    void delete(UserInfo userInfo);

    @Update
    public void updateOrgs(UserInfo... userInfos);

    @Query("SELECT * FROM UserInfo order by uid asc")
    List<UserInfo> getAll();

    @Query("SELECT * FROM UserInfo where uid=:uid order by uid asc limit 1")
    UserInfo getUserInfo(String uid);

}
