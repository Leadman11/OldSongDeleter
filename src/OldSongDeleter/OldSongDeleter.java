package OldSongDeleter;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class OldSongDeleter {

    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File OLD_SONGS_FOLDER = Utils.join(CWD, "OLD SONGS");
    public static final int LAST_ACCEPTABLE_KEY = Integer.parseInt("29a8", 16);

//    static File join(File first, String... others) {
//        return Paths.get(first.getPath(), others).toFile();
//    }

    public static int getID(String filename) {
        String temp = filename.substring(0, filename.indexOf(" ") + 2);
        String idStr;

        if (temp.charAt(temp.length() - 1) == '(') {
            idStr = temp.substring(0, temp.indexOf(" "));
        } else {
            return -1;
        }

        return Integer.parseInt(idStr, 16);
    }

    public static void main(String[] args) {
        File[] allFiles = CWD.listFiles();
        OLD_SONGS_FOLDER.mkdir();
        String idStr;
        int id;

        for (int i = 0; i < allFiles.length; i++) {

            String filename = allFiles[i].getName();
            id = getID(filename);

            if (id == -1) {
                continue;
            }

            if (id <= LAST_ACCEPTABLE_KEY) {
                // Move to OLD_SONGS_FOLDER
                // copy dir (apache needed) - https://stackoverflow.com/questions/6214703/copy-entire-directory-contents-to-another-directory
                File song = Utils.join(OLD_SONGS_FOLDER, filename);

                try {
                    Utils.copyDirectoryCompatibityMode(allFiles[i], song);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Utils.deleteDir(allFiles[i]);
            } else {
                continue;
            }
        }
    }
}
