package com.yuanzhy.tools.template.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author yuanzhy
 * @date 2020/1/26
 */
public class ZipUtil {
    // --------------------------------- 压缩 ---------------------------------
    /** zip文件后缀 */
    public static final String EXT = ".zip";

    // 符号"/"用来作为目录标识判断符
    private static final String PATH = "/";
    /**
     * 压缩, zip文件默认生成在同目录
     *
     * @param srcPath
     * @throws Exception
     */
    public static void zip(String srcPath) {
        zip(new File(srcPath));
    }

    /**
     * 压缩, zip文件默认生成在同目录
     *
     * @param srcFile
     * @throws Exception
     */
    public static void zip(File srcFile) {
        String destPath = srcFile.getAbsolutePath().concat(EXT);
        zip(srcFile, destPath);
    }

    /**
     * 文件压缩
     *
     * @param srcPath
     *            源文件路径
     * @param destPath
     *            目标文件路径
     *
     */
    public static void zip(String srcPath, String destPath) {
        File srcFile = new File(srcPath);
        zip(srcFile, destPath);
    }

    /**
     * 压缩文件
     *
     * @param srcFile  源文件
     * @param destPath 目标路径
     * @throws Exception
     */
    public static void zip(File srcFile, String destPath) {
        zip(srcFile, new File(destPath));
    }

    /**
     * 压缩
     *
     * @param srcFile
     *            源文件
     * @param destFile
     *            目标文件
     * @throws Exception
     */
    public static void zip(File srcFile, File destFile) {
        // 对输出文件做CRC32校验
        try (ZipOutputStream zos = new ZipOutputStream(
                new CheckedOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(destFile)), new CRC32()))) {
            zip(srcFile, zos, "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 压缩
     *
     * @param srcFile
     *            源路径
     * @param zos
     *            ZipOutputStream
     * @param basePath
     *            压缩包内相对路径
     * @throws Exception
     */
    private static void zip(File srcFile, ZipOutputStream zos, String basePath) throws IOException  {
        if (srcFile.isDirectory()) {
            zipDir(srcFile, zos, basePath);
        } else {
            zipFile(srcFile, zos, basePath);
        }
    }

    /**
     * 压缩目录
     *
     * @param dir
     * @param zos
     * @param basePath
     * @throws Exception
     */
    private static void zipDir(File dir, ZipOutputStream zos,
                               String basePath) throws IOException {

        File[] files = dir.listFiles();
        // 构建空目录
        if (files.length < 1) {
            ZipEntry entry = new ZipEntry(basePath + dir.getName() + PATH);
            entry.setTime(dir.lastModified());
            zos.putNextEntry(entry);
            zos.closeEntry();
        }
        for (File file : files) {
            // 递归压缩
            zip(file, zos, basePath + dir.getName() + PATH);
        }
    }

    /**
     * 文件压缩
     *
     * @param file
     *            待压缩文件
     * @param zos
     *            ZipOutputStream
     * @param dir
     *            压缩文件中的当前路径
     * @throws Exception
     */
    private static void zipFile(File file, ZipOutputStream zos, String dir)
            throws IOException {
        ZipEntry entry = new ZipEntry(dir + file.getName());
        entry.setTime(file.lastModified());
        zos.putNextEntry(entry);

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            IOUtils.copy(bis, zos);
        } finally {
            zos.closeEntry();
        }
    }

    // --------------------------------- 解压 ---------------------------------

    /**
     * 解压, 默认解压到同目录
     * @param zipPath zip路径
     */
    public static void unzip(String zipPath) {
        unzip(new File(zipPath));
    }
    /**
     * 解压, 默认解压到同目录
     * @param zipFile zip File
     */
    public static void unzip(final File zipFile) {
        File destFile = new File(zipFile.getParentFile(), FilenameUtils.getBaseName(zipFile.getName()));
        unzip(zipFile, destFile);
    }

    /**
     * 解压
     * @param zipPath  zip路径
     * @param destPath 解压后路径
     */
    public static void unzip(String zipPath, String destPath) {
        unzip(new File(zipPath), destPath);
    }

    /**
     * 解压
     * @param zipFile  zipFile
     * @param destPath 解压后目录
     */
    public static void unzip(final File zipFile, String destPath) {
        unzip(zipFile, new File(destPath));
    }
    /**
     * 解压
     * @param zipFile  zipFile
     * @param destFile 解压后File
     */
    public static void unzip(final File zipFile, final File destFile) {
        unzip(zipFile, destFile, true);
    }
    /**
     * 解压
     * @param zipFile  zipFile
     * @param destFile 解压后File
     * @param noParent 是否没有一级目录(为false则额外生成一个zip文件名的同名目录作为解压后的根目录)
     */
    public static void unzip(final File zipFile, final File destFile, final boolean noParent) {
        try (InputStream read = new FileInputStream(zipFile)) {
            unzip(read, destFile, noParent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 解压
     * @param is       zip流
     * @param destFile 解压后File
     * @param noParent 是否没有一级目录(为false则额外生成一个zip文件名的同名目录作为解压后的根目录)
     */
    public static void unzip(final InputStream is, final File destFile, final boolean noParent) throws IOException {
        if (destFile.exists()) {
            throw new IllegalArgumentException("目标文件已存在: " + destFile.getAbsolutePath());
        }
        final ZipInputStream in = new ZipInputStream(is);
        ZipEntry entry;
        while ((entry = in.getNextEntry()) != null) {
            String path = entry.getName();
            if (noParent) {
                path = path.replaceFirst("^[^/]+/", "");
            }
            final File file = new File(destFile, path);
            if (entry.isDirectory()) {
                file.mkdirs();
                continue;
            }
            file.getParentFile().mkdirs();
            try (FileOutputStream fos = new FileOutputStream(file)) {
                IOUtils.copy(in, fos);
            }
            final long lastModified = entry.getTime();
            if (lastModified > 0) {
                file.setLastModified(lastModified);
            }
        }
    }
}
