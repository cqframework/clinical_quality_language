package org.hl7.cql.model;

public class SearchType {

    public SearchType(String name, String path, DataType type) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("A name is required to construct a Search");
        }
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("A path is required to construct a Search");
        }

        this.name = name;
        this.path = path;
        this.type = type;
    }

    private String name;

    public String getName() {
        return name;
    }

    private String path;

    public String getPath() {
        return path;
    }

    private DataType type;

    public DataType getType() {
        return type;
    }
}
