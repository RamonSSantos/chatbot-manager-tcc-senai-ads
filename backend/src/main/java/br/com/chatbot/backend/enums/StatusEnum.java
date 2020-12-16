package br.com.chatbot.backend.enums;

public enum StatusEnum {
    ENABLED(1),
    DISABLED(2),
    APPROVAL(3);

    private final int value;

    StatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
