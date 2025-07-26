package logger.enums;

public enum Severity   {

    HIGH("high"),
    WARN("warn"),
    LOW("low");

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    Severity(String name) {
        this.name = name;
    }
}
