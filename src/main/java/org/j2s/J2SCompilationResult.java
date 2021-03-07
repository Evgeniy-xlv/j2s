package org.j2s;

import java.io.File;
import java.util.Objects;

public class J2SCompilationResult {

    private final Class<?> type;
    private final File file;
    private final String content;

    public J2SCompilationResult(Class<?> type, File file, String content) {
        this.type = type;
        this.file = file;
        this.content = content;
    }

    public Class<?> getType() {
        return type;
    }

    public File getFile() {
        return file;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        J2SCompilationResult that = (J2SCompilationResult) o;
        return Objects.equals(type, that.type) && Objects.equals(file, that.file) && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, file, content);
    }
}
