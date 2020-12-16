package br.com.chatbot.backend.enums;

public enum ProfileEnum {
    ADMINISTRATOR(1),
    SPECIALIST(2),
    EMPLOYEE(3);

    private final int value;

    ProfileEnum(int value) { this.value = value; }

    public int getValue() { return value; }
}
