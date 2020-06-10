package com.spring.service;

import com.vdurmont.emoji.EmojiParser;

public class Constant {

    public static class Command {
        public static final String ADD_FILTER = "Добавить фильтр";
        public static final String ALL_FILTER = "Показать все фильтра";
        public static final String DELETE_FILTER = "Удалить фильтра";
    }


    public enum Emoji {
        PLUS(":heavy_plus_sign:"),
        MINUS(":heavy_minus_sign:"),
        LIKE(":like:");

        private String value;

        public String get() {
            return EmojiParser.parseToUnicode(value);
        }

        Emoji(String value) {
            this.value = value;
        }
    }
}
