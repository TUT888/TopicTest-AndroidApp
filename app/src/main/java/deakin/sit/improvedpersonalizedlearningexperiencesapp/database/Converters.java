package deakin.sit.improvedpersonalizedlearningexperiencesapp.database;

import android.text.TextUtils;

import androidx.room.TypeConverter;

public class Converters {
    @TypeConverter
    public static String fromStringArray(String[] array) {
        return array == null ? null : TextUtils.join(",", array);
    }

    @TypeConverter
    public static String[] toStringArray(String data) {
        return data == null ? null : data.split(",");
    }
}