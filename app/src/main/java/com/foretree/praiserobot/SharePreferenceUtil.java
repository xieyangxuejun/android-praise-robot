package com.foretree.praiserobot;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by silen on 13/03/2017.
 */

public class SharePreferenceUtil {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private SharePreferenceUtil(Context context, String name) {
        prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static SharePreferenceUtil getPrefs(Context context, String name) {
        return new SharePreferenceUtil(context,name);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return prefs.getBoolean(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return prefs.getFloat(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return prefs.getInt(key, defValue);
    }

    public long getLong(String key, long defValue) {
        return prefs.getLong(key, defValue);
    }

    public String getString(String key, String defValue) {
        return prefs.getString(key, defValue);
    }

    public Object getObject(String key) {
        try {
            String stringBase64 = prefs.getString(key, "");
            if (TextUtils.isEmpty(stringBase64))
                return null;

            byte[] base64Bytes = Base64.decode(stringBase64, 0);
            ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public SharePreferenceUtil putBoolean(String key, boolean v) {
        ensureEditorAvailability();
        editor.putBoolean(key, v);
        return this;
    }

    public SharePreferenceUtil putFloat(String key, float v) {
        ensureEditorAvailability();
        editor.putFloat(key, v);
        return this;
    }

    public SharePreferenceUtil putInt(String key, int v) {
        ensureEditorAvailability();
        editor.putInt(key, v);
        return this;
    }

    public SharePreferenceUtil putLong(String key, long v) {
        ensureEditorAvailability();
        editor.putLong(key, v);
        return this;
    }

    public SharePreferenceUtil putString(String key, String v) {
        ensureEditorAvailability();
        editor.putString(key, v);
        return this;
    }

    public SharePreferenceUtil putObject(String key, Object obj) {
        ensureEditorAvailability();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);

            String stringBase64 = Base64.encodeToString(baos.toByteArray(), 0);
            editor.putString(key, stringBase64);
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public void save() {
        if (editor != null) {
            editor.apply();
        }
    }

    private void ensureEditorAvailability() {
        if (editor == null) {
            editor = prefs.edit();
        }
    }

    public void remove(String key) {
        ensureEditorAvailability();
        editor.remove(key);
        save();
    }

    public void clear() {
        ensureEditorAvailability();
        editor.clear();
        save();
    }
}