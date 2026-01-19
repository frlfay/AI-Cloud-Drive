package net.xdclass.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum FileTypeEnum {
    COMMON("common"),
    COMPRESS("compress"),
    EXCEL("excel"),
    WORD("word"),
    PDF("pdf"),
    TXT("txt"),
    IMG("img"),
    AUDIO("audio"),
    VIDEO("video"),
    PPT("ppt"),
    CODE("code"),
    CSV("csv");

    private final String type;

    private static final Map<String, FileTypeEnum> EXTENSION_MAP = new HashMap<>();

    static {
        for (FileTypeEnum fileType : values()) {
            switch (fileType) {
                case COMPRESS:
                    EXTENSION_MAP.put("zip", fileType);
                    EXTENSION_MAP.put("rar", fileType);
                    EXTENSION_MAP.put("7z", fileType);
                    break;
                case EXCEL:
                    EXTENSION_MAP.put("xls", fileType);
                    EXTENSION_MAP.put("xlsx", fileType);
                    break;
                case WORD:
                    EXTENSION_MAP.put("doc", fileType);
                    EXTENSION_MAP.put("docx", fileType);
                    break;
                case PDF:
                    EXTENSION_MAP.put("pdf", fileType);
                    break;
                case TXT:
                    EXTENSION_MAP.put("txt", fileType);
                    break;
                case IMG:
                    EXTENSION_MAP.put("jpg", fileType);
                    EXTENSION_MAP.put("jpeg", fileType);
                    EXTENSION_MAP.put("png", fileType);
                    EXTENSION_MAP.put("gif", fileType);
                    EXTENSION_MAP.put("bmp", fileType);
                    break;
                case AUDIO:
                    EXTENSION_MAP.put("mp3", fileType);
                    EXTENSION_MAP.put("wav", fileType);
                    EXTENSION_MAP.put("aac", fileType);
                    break;
                case VIDEO:
                    EXTENSION_MAP.put("mp4", fileType);
                    EXTENSION_MAP.put("avi", fileType);
                    EXTENSION_MAP.put("mkv", fileType);
                    break;
                case PPT:
                    EXTENSION_MAP.put("ppt", fileType);
                    EXTENSION_MAP.put("pptx", fileType);
                    break;
                case CODE:
                    EXTENSION_MAP.put("java", fileType);
                    EXTENSION_MAP.put("c", fileType);
                    EXTENSION_MAP.put("cpp", fileType);
                    EXTENSION_MAP.put("py", fileType);
                    EXTENSION_MAP.put("js", fileType);
                    EXTENSION_MAP.put("html", fileType);
                    EXTENSION_MAP.put("css", fileType);
                    break;
                case CSV:
                    EXTENSION_MAP.put("csv", fileType);
                    break;
                default:
                    break;
            }
        }
    }

    FileTypeEnum(String type) {
        this.type = type;
    }

    public static FileTypeEnum fromExtension(String extension) {
        if (extension == null || extension.isEmpty() || !isValidExtension(extension)) {
            return COMMON;
        }

        try {
            return EXTENSION_MAP.getOrDefault(extension.toLowerCase(), COMMON);
        } catch (NullPointerException e) {
            // 记录日志
            try {
                System.err.println("Unexpected null pointer exception: " + e.getMessage());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
            }
            return COMMON;
        }
    }

    private static boolean isValidExtension(String extension) {
        // 确保扩展名只包含字母和数字
        return extension.matches("[a-zA-Z0-9]+");
    }
}
