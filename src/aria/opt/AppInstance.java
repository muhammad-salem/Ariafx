package aria.opt;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.apache.commons.io.FileUtils;

public class AppInstance {
//	private static File file;
    private static FileChannel channel;
    private static FileLock lock;
    
	public static void initFileLock() {
		 isAppInstanceExists();
	}

    
    @SuppressWarnings("resource")
	public static boolean isAppInstanceExists() {
    	
    	File file = new  File(R.LockFile);
		try {
			if(!file.exists()){
				FileUtils.forceMkdir(file.getParentFile());
				file.createNewFile();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

/*   	
    	file = new File(R.LockFile);
		if (file.exists()) {
            // if exist try to delete it
            System.out.println("delete file " + file.delete());
        }
*/		
		
		// Try to get the lock
       
		try {
			//File file = new  File(R.LockFile);
			//file.createNewFile();
			channel = new RandomAccessFile(R.LockFile, "rw").getChannel();
			lock = channel.tryLock();
//			channel.close();
            if(lock == null){
                // File is lock by other application
               
//                file.delete();
                return true;							// run app
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
            if(lock != null) {
            	//if(!lock.isValid())return;
                lock.release();
                channel.close();
//                file.delete();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
 
    static class ShutdownHookThread extends Thread {
 
        public void run() {
            unlockFile();
        }
    }
}
