package com.example.aura.core;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private static final String FILE = "aura_prefs";
    private static final String KEY_NAME = "user_name";
    private static final String KEY_PHONE = "user_phone";

    public static boolean hasProfile(Context ctx) {
        return getName(ctx) != null && !getName(ctx).isEmpty();
    }

    public static void saveProfile(Context ctx, String name, String phone) {
        SharedPreferences sp = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_NAME, name).putString(KEY_PHONE, phone == null ? "" : phone).apply();
    }

    public static String getName(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE);
        return sp.getString(KEY_NAME, "");
    }

    public static String getPhone(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE);
        return sp.getString(KEY_PHONE, "");
    }
}

