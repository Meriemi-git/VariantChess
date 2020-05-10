package fr.aboucorp.variantchess.app.utils;

import android.text.TextUtils;

import androidx.room.TypeConverter;

public class AuthTypeConverter {
    @TypeConverter
    public static String toString(AuthType value) {
        return value == null ? null : value.toString();
    }

    @TypeConverter
    public static AuthType toEnum(String value) {
        return TextUtils.isEmpty(value) ? null : AuthType.valueOf(value);
    }
}
