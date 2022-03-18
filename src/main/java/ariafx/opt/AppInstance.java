package ariafx.opt;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class AppInstance {

    private static FileChannel channel;
    private static FileLock lock;

    public static void initFileLock() {
        isAppInstanceExists();
    }


    @SuppressWarnings("resource")
    public static boolean isAppInstanceExists() {

        File file = new File(R.LockFile);
        try {
            if (!file.exists()) {
                FileUtils.forceMkdir(file.getParentFile());
                file.createNewFile();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        // Try to get the lock

        try {
            channel = new RandomAccessFile(R.LockFile, "rw").getChannel();
            lock = channel.tryLock();
            if (lock == null) {
                // File is lock by other application
                return true; // run app
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        ShutdownHookThread shutdownHook = new ShutdownHookThread();
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        return false;
    }

    private static void unlockFile() {
        // release and delete file lock
        try {
            if (lock != null) {
                lock.release();
                channel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ShutdownHookThread extends Thread {
        @Override
        public void run() {
            unlockFile();
        }
    }
}
