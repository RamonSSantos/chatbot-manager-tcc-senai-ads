package br.com.chatbot.backend.enums;

public enum ContentLogStatusEnum {
    CREATE(1),
    EDIT(2);

    private final int value;

    ContentLogStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
