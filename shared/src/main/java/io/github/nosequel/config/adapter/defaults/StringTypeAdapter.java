package io.github.nosequel.config.adapter.defaults;

import io.github.nosequel.config.adapter.ConfigTypeAdapter;

public class StringTypeAdapter implements ConfigTypeAdapter<String> {

    /**
     * Convert a string back to the object
     *
     * @param string the string to convert back
     * @return the converted object
     */
    @Override
    public String convert(String string) {
        return translateAlternateColorCodes('&', string.replace("§", "&"));
    }

    /**
     * Translate alternate color codes method from Bukkit's ChatColor enum
     *
     * @param altColorChar    the alternate color character
     * @param textToTranslate the text to tranlsate the color codes of
     * @return the translated text
     */
    private String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();

        for (int i = 0; i < b.length - 1; ++i) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }

        return new String(b);
    }
}