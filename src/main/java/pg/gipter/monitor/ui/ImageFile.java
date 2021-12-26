package pg.gipter.monitor.ui;

import java.util.*;

public enum ImageFile {

    MINION_TRAY("png/minion.png");

    private final String fileName;

    ImageFile(String fileName) {
        this.fileName = fileName;
    }

    public String fileUrl() {
        return fileName;
    }

    public static ImageFile randomImage(Set<ImageFile> set) {
        Random random = new Random();
        int imageIdx = random.nextInt(set.size());
        return new ArrayList<>(set).get(imageIdx);
    }

    public static ImageFile randomPartialSuccessImage() {
        return randomImage(EnumSet.of(
                MINION_TRAY
        ));
    }

    public static ImageFile randomSuccessImage() {
        return randomImage(EnumSet.of(
                MINION_TRAY
        ));
    }

    public static ImageFile randomFailImage() {
        return randomImage(EnumSet.of(
                MINION_TRAY
        ));
    }
}
