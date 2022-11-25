package brainwind.letstalksample.data.database.org;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Org
{

    //The Org Name "_" Scope "_" Category
    @PrimaryKey
    @NonNull
    public String orgid;

    @ColumnInfo(name = "org_name")
    public String OrgName;

    @ColumnInfo(name = "org_desc")
    public String OrgDesc;

    @ColumnInfo(name = "org_scope")
    public String OrgScope;

    @ColumnInfo(name = "org_country")
    public String OrgCountry;

    @ColumnInfo(name = "org_country_code")
    public String OrgCountryCode;

    @ColumnInfo(name = "org_region")
    public String OrgRegion;

    @ColumnInfo(name = "has_o_o_a")
    public boolean HasOtherPartsInOtherAreas;

    @ColumnInfo(name = "org_cat")
    public String OrgCategory;

    @ColumnInfo(name = "org_main_color")
    public String OrgMainColor;

    @ColumnInfo(name = "owner")
    public String Owner;

    @ColumnInfo(name = "local_logo_path",defaultValue = "")
    public String LocalLogoPath;

    @ColumnInfo(name = "online_logo_path",defaultValue = "")
    public String OnlineLogoPath;

    @ColumnInfo(name = "num_members",defaultValue = "0")
    public int num_members;

    @ColumnInfo(name = "num_likes",defaultValue = "0")
    public int num_likes;

    @ColumnInfo(name = "user_external_profile_image",defaultValue = "0")
    public boolean user_external_profile_image;


}
