package localization.entity;

public class LocalizationStringEntity {
    private int id;
    private String key;
    private String value;
    private String language;

    public LocalizationStringEntity() {}

    public LocalizationStringEntity(String key, String value, String language) {
        this.key = key;
        this.value = value;
        this.language = language;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}

