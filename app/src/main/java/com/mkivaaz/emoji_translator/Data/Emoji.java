package com.mkivaaz.emoji_translator.Data;

public class Emoji {

    String emojiName;
    int emojiCode;

    public Emoji() {
    }

    public Emoji(String emojiName, int emojiCode) {
        this.emojiName = emojiName;
        this.emojiCode = emojiCode;
    }

    public String getEmojiName() {
        return emojiName;
    }

    public void setEmojiName(String emojiName) {
        this.emojiName = emojiName;
    }

    public int getEmojiCode() {
        return emojiCode;
    }

    public void setEmojiCode(int emojiCode) {
        this.emojiCode = emojiCode;
    }
}
