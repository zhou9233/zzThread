package cn.zz.NIO.channels;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;

/**
 * Create a file with holes in it.
 * <p>
 * Created April 2002
 *
 * @author Ron Hitchens (ron@ronsoft.com)
 * @version $Id: FileHole.java,v 1.2 2002/05/19 04:55:45 ron Exp $
 */
public class FileHole {
    public static void main(String[] argv)
            throws IOException {
        // create a temp file, open for writing and get a FileChannel
        //File temp = File.createTempFile ("holy", null);
        File temp = new File("C:\\\\Users\\\\Administrator\\\\Desktop\\\\websocketBim\\\\1441795\\\\test");
        RandomAccessFile file = new RandomAccessFile(temp, "rw");
        FileChannel channel = file.getChannel();
        // create a working buffer
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(100);
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    putData(0, byteBuffer, channel);
                    System.out.println(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread1.start();
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    putData(200, byteBuffer, channel);
                    System.out.println(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread2.start();

        //putData (0, byteBuffer, channel);
        putData (100, byteBuffer, channel);
        //putData (200, byteBuffer, channel);
        // Size will report the largest position written, but
        // there are two holes in this file.  This file will
        // not consume 5MB on disk (unless the filesystem is
        // extremely brain-damaged).
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Wrote temp file '" + temp.getPath()
                + "', size=" + channel.size());


        channel.close();
        file.close();
    }

    private synchronized static void putData(int position, ByteBuffer buffer,
                                FileChannel channel)
            throws IOException {
        String string = "*<-- location " + position;
        buffer.clear();
        buffer.put(string.getBytes("US-ASCII"));
        buffer.flip();

        //改变文件大小或position改变的操作是线程不安全的
        channel.position(position);
        channel.write(buffer);


    }
}