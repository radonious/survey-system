package edu.eltex.forms;

public class FileStorageUtils {

    /**
     * Определяет путь до папки с изображениями для опросов
     * @return путь до папки изображений
     */
    public static String getUploadDir() {
        return System.getProperty("user.dir") + "/uploads/images";
    }
}
