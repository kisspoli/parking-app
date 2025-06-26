package android.os;

import java.util.Locale;

/** @noinspection ALL*/
public class LocaleList {
    private final Locale[] locales;

    public LocaleList(Locale... locales) {
        this.locales = locales;
    }

    public Locale get(int index) {
        return locales[index];
    }

    public int size() {
        return locales.length;
    }
}
