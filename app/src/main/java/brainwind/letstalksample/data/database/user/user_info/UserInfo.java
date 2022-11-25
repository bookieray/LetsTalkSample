package brainwind.letstalksample.data.database.user.user_info;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserInfo
{

    @PrimaryKey
    @NonNull
    public String uid;

    @ColumnInfo(name = "firstname")
    public String firstname;

    @ColumnInfo(name = "lastname")
    public String lastname;

    @ColumnInfo(name = "profile_local_path")
    public String profile_local_path;

    @ColumnInfo(name = "profile_path")
    public String profile_path;

    @ColumnInfo(name = "country_code",defaultValue = "27")
    public int country_code;

    @ColumnInfo(name = "cellphone",defaultValue = "27")
    public String cellphone;

    @ColumnInfo(name = "user_external_profile_image",defaultValue = "0")
    public boolean user_external_profile_image;


}
