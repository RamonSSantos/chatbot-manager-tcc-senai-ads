package br.com.chatbot.backend.enums;

public enum MessageLogStatusEnum {
    QUESTION(1),
    ANSWER(2);

    private final int value;

    MessageLogStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static MessageLogStatusEnum fromId(int id) {
        for (MessageLogStatusEnum type : values()) {
            if (type.getValue() == id) {
                return type;
            }
        }
        return null;
    }
}
