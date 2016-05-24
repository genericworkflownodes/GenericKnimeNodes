/**
 * Copyright (c) 2011, Marc Röttig, Stephan Aiche.
 * Copyright (c) 2016, Benjamin Schubert.
 *
 * This file is part of GenericKnimeNodes.
 * 
 * GenericKnimeNodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.genericworkflownodes.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Collection of Helper methods.
 * 
 * @author roettig, aiche, schubert
 */
public final class Helper {

    /**
     * Size of the buffer when copying streams to files.
     */
    private static final int BUFFER_SIZE = 2048;
	private static final String OS_WIN = "Windows";
	private static final String OS_MAC = "Mac";

    /**
     * Private c'tor to avoid instantiation of Util class.
     */
    private Helper() {
    }

    /**
     * Copies the content of the {@link InputStream} in to the {@link File}
     * dest.
     * 
     * @param in
     *            Stream to copy.
     * @param dest
     *            File to put content of stream into.
     * @throws IOException
     *             Is thrown if operations on target file fail.
     */
    public static void copyStream(final InputStream in, final File dest)
            throws IOException {
        FileOutputStream out = new FileOutputStream(dest);
        BufferedInputStream bin = new BufferedInputStream(in);

        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = bin.read(buffer, 0, 2048)) != -1) {
                out.write(buffer, 0, len);
            }
            out.close();
            bin.close();
        } catch (IOException ex) {
            // try to close the streams
            out.close();
            bin.close();

            // rethrow exception
            throw ex;
        }

    }

    /**
     * Local random number generator to ensure uniqueness of file names.
     */
    private static Random randomNumberGenerator = new Random();

    /**
     * Creates a temporary file in the given directory, with the given suffix.
     * 
     * @param directory
     *            Directory to create the file in.
     * @param suffix
     *            File suffix/extension.
     * @param autodelete
     *            Indicator if the created file should be deleted when exiting
     *            the JVM.
     * @return A {@link File} object pointing to the new file.
     * @throws IOException
     *             In case of problems when creating the file.
     */
    public static synchronized File getTempFile(final String directory,
            final String suffix, boolean autodelete) throws IOException {

        int num = randomNumberGenerator.nextInt(Integer.MAX_VALUE);
        File file = new File(directory + File.separator
                + String.format("%06d.%s", num, suffix));
        while (file.exists()) {
            num = randomNumberGenerator.nextInt(Integer.MAX_VALUE);
            file = new File(directory + File.separator
                    + String.format("%06d.%s", num, suffix));
        }

        if (!file.createNewFile()) {
            throw new IOException("Failed to create file "
                    + file.getAbsolutePath());
        }

        if (autodelete) {
            file.deleteOnExit();
        }

        return file;
    }

    /**
     * Creates a temporary file in the systems temporary directory with the
     * given suffix.
     * 
     * @param suffix
     *            File suffix/extension.
     * @param autodelete
     *            Indicator if the created file should be deleted when exiting
     *            the JVM.
     * @return A {@link File} object pointing to the new file.
     * @throws IOException
     *             In case of problems when creating the file.
     */
    public static synchronized File getTempFile(final String suffix,
            boolean autodelete) throws IOException {
        File file = File.createTempFile("GKN", suffix);
        if (autodelete) {
            file.deleteOnExit();
        }
        return file;
    }

    /**
     * Creates a temporary directory in the given directory with the given
     * prefix.
     * 
     * @param directory
     *            The directory to create the directory in.
     * @param prefix
     *            The prefix of the generated directory.
     * @param autodelete
     *            If true the file will be deleted when JVM shuts down.
     * @return A {@link File} pointing to the newly generated directory.
     * @throws IOException
     *             In case of problems when creating the directory.
     */
    public static synchronized File getTempDir(final String directory,
            final String prefix, boolean autodelete) throws IOException {

        int num = randomNumberGenerator.nextInt(Integer.MAX_VALUE);
        File dir = new File(directory + File.separator
                + String.format("%s%06d", prefix, num));
        while (dir.exists()) {
            num = randomNumberGenerator.nextInt(Integer.MAX_VALUE);
            dir = new File(directory + File.separator
                    + String.format("%s%06d", prefix, num));
        }
        dir.mkdirs();

        if (autodelete) {
            dir.deleteOnExit();
        }

        return dir;
    }

    /**
     * Creates a temporary directory in the systems temporary directory with the
     * given prefix.
     * 
     * @param prefix
     *            The prefix of the generated directory.
     * @param autodelete
     *            If true the file will be deleted when JVM shuts down.
     * @return A {@link File} pointing to the newly generated directory.
     * @throws IOException
     *             In case of problems when creating the directory.
     */
    public static synchronized File getTempDir(final String prefix,
            boolean autodelete) throws IOException {
    	return getTempDir(System.getProperty("java.io.tmpdir"), prefix,
    				autodelete);
    }

    /**
     * Reads the first lines of the file into a string. At maximum maxLines will
     * be read.
     * 
     * @param file
     *            The file to read.
     * @param maxLines
     *            The number of maximal lines to read.
     * @return The string containing the first maxLines lines.
     * @throws IOException
     *             if the file does not exist or cannot be opened or read.
     */
    public static String readFileSummary(final File file, int maxLines)
            throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        StringBuffer sb = new StringBuffer();
        try {

            String line = "";

            int cnt = 0;

            sb.append("File path: ").append(file.getAbsolutePath())
                    .append(System.getProperty("line.separator"));
            sb.append("File size: ").append(file.length()).append(" bytes")
                    .append(System.getProperty("line.separator"));

            Date date = new Date(file.lastModified());
            Format formatter = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss");
            String s = formatter.format(date);

            sb.append("File time: ").append(s)
                    .append(System.getProperty("line.separator"));

            sb.append(String.format("File content (first %d lines):", maxLines))
                    .append(System.getProperty("line.separator"));

            while ((line = br.readLine()) != null) {
                sb.append(line + System.getProperty("line.separator"));
                cnt++;
                if (cnt > maxLines) {
                    sb.append("######### OUTPUT TRUNCATED #########").append(
                            System.getProperty("line.separator"));
                    break;
                }
            }
        } catch (IOException ex) {
            // close readers
            br.close();
            fr.close();
            // rethrow
            throw ex;
        }

        // close readers
        br.close();
        fr.close();

        return sb.toString();
    }

    /**
     * Copies the content of to_copy into target. Assumes that src and target
     * have the same size.
     * 
     * @param src
     * @param target
     */
    public static void array2dcopy(final String[][] src, final String[][] target) {
        assert (target.length == src.length);

        for (int i = 0; i < target.length; ++i) {
            int array_length = src[i].length;
            target[i] = new String[src[i].length];
            System.arraycopy(src[i], 0, target[i], 0, array_length);
        }
    }
    
    /***
     * Checks if OS is specific OS family
     * @return
     */
    private static boolean isOS(final String osName) {
        return System.getProperty("os.name").startsWith(osName);
    }
    
    /**
     * Checks if OS is part of the Windows-Family
     * @return
     */
    public static boolean isWin(){
    	return isOS(OS_WIN);
    }
    
    /**
     * Checks if OS is part of the Mac-Family
     * @return
     */
     public static boolean isMac(){
    	return isOS(OS_MAC);
    }
}
