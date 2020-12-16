package br.com.chatbot.backend.enums;

public enum MonitoringLogStatusEnum {
    SCHEDULE(1, "Agendado"),
    SEND(2, "Enviado"),
    RESTART(3, "Reiniciado"),
    CANCEL(4, "Cancelado"),
    INCOMPLETE(5, "Incompleto");

    private final int value;
    private final String description;

    MonitoringLogStatusEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static MonitoringLogStatusEnum fromId(int id) {
        for (MonitoringLogStatusEnum type : values()) {
            if (type.getValue() == id) {
                return type;
            }
        }
        return null;
    }
}
