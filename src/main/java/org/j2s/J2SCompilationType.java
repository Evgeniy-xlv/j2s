package org.j2s;

public enum J2SCompilationType {

    @Deprecated
    JAVASCRIPT("js"),
    TYPESCRIPT("ts");

    private final String fileExtension;

    J2SCompilationType(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
