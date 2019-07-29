package com.multiplies.multiring.ringtoneItem;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

public class RingtoneItem {

    private Uri uri;
    private Ringtone ringtone;
    private String title;
    private boolean active;

    public RingtoneItem(Context context, Uri uri, boolean active) {

        this.uri = uri;
        ringtone = RingtoneManager.getRingtone(context, uri);
        title = ringtone.getTitle(context);
        this.active = active;

        if (title.startsWith("Default ringtone (") && title.endsWith(")")) {
            title = title.substring(18, title.length() - 1);
        }


    }

    public Uri getUri() {
        return uri;
    }

    public Ringtone getRingtone() {
        return ringtone;
    }

    public String getTitle() {
        return title;
    }

    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "RingtoneItem{" +
                "uri=" + uri +
                ", active=" + active +
                '}';
    }

    public static RingtoneItem parse(Context context, String string) {
        Uri uri = Uri.parse(string.substring(17, string.lastIndexOf(", active=")));
        boolean active = Boolean.parseBoolean(string.substring(string.lastIndexOf("=") + 1, string.length() - 1));

        return new RingtoneItem(context, uri, active);
    }
}
