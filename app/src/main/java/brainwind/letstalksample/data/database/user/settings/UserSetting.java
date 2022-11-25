package brainwind.letstalksample.data.database.user.settings;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserSetting
{

    @PrimaryKey
    @NonNull
    public String uid;

    @NonNull
    @ColumnInfo(name = "setting_name")
    public String setting_name;

    @NonNull
    @ColumnInfo(name = "setting")
    public boolean setting;



}


