package OldSongDeleter;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class OldSongDeleter {

    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File OLD_SONGS_FOLDER = join(CWD, "OLD SONGS");
    public static final int LAST_ACCEPTABLE_KEY = Integer.parseInt("29a8", 16);

    static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }

    static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (! Files.isSymbolicLink(f.toPath())) {
                    deleteDir(f);
                }
            }
        }
        file.delete();
    }
    
    // Copy Directory https://www.baeldung.com/java-copy-directory
    private static void copyDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
        if (!destinationDirectory.exists()) {
            destinationDirectory.mkdir();
        }
        for (String f : sourceDirectory.list()) {
            copyDirectoryCompatibityMode(new File(sourceDirectory, f), new File(destinationDirectory, f));
        }
    }

    public static void copyDirectoryCompatibityMode(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            copyDirectory(source, destination);
        } else {
            copyFile(source, destination);
        }
    }

    private static void copyFile(File sourceFile, File destinationFile)
            throws IOException {
        try (InputStream in = new FileInputStream(sourceFile);
             OutputStream out = new FileOutputStream(destinationFile)) {
            byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
        }
    }

    public static void main(String[] args) {
        File[] allFiles = CWD.listFiles();
        OLD_SONGS_FOLDER.mkdir();
        String idStr;
        int id;

        for (int i = 0; i < allFiles.length; i++) {

            String filename = allFiles[i].getName();
            String temp = filename.substring(0, filename.indexOf(" ") + 2);

            // Get ID
            if (temp.charAt(temp.length() - 1) == '(') {
                idStr = temp.substring(0, temp.indexOf(" "));
            } else {
                continue;
            }

            id = Integer.parseInt(idStr, 16);

            if (id <= LAST_ACCEPTABLE_KEY) {
                // Move to OLD_SONGS_FOLDER
                // copy dir (apache needed) - https://stackoverflow.com/questions/6214703/copy-entire-directory-contents-to-another-directory
                File song = join(OLD_SONGS_FOLDER, filename);

                try {
                    copyDirectory(allFiles[i], song);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                deleteDir(allFiles[i]);
            } else {
                continue;
            }
        }
    }
}
