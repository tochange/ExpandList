package com.richie.expandable;

import android.content.Context;
import android.support.annotation.Keep;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.richie.expandable.entity.Item;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public final class Util {
    public static ArrayList<Classify> getMenus(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.bottom_menu_initial_data);

        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            final Gson gson = new Gson();
            return gson.fromJson(new String(buffer, "UTF-8"),
                    new TypeToken<ArrayList<Classify>>() {
                    }.getType());
        } catch (IOException e) {
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    @Keep
    public class Classify {
        public String classify;
        public ArrayList<Item> items;
    }

}
