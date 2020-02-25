package com.ezsyncxz.efficiency.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.*;

/**
 * @ClassName CompressUtils
 * @Description 对文件夹进行归档压缩或者对压缩文件进行解压
 * @Author chenwj
 * @Date 2020/2/24 15:54
 * @Version 1.0
 **/

public class CompressUtils {

    private static final Logger logger = LoggerFactory.getLogger(CompressUtils.class);

    private static boolean isCreateSrcDir = true;//是否创建源目录

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String src = "D:\\chenwj\\document\\pics\\在物理隔离环境下，如何提高文件传输同步的效率？";//指定压缩源，可以是目录或文件
        String decompressDir = "D:\\chenwj\\document\\pics\\测试";//解压路径
        String archive = "D:\\chenwj\\document\\pics\\测试用文件.zip";//压缩包路径
        String comment = "Java Zip 测试.";//压缩包注释

        //----压缩文件或目录
        writeByApacheZipOutputStream(src, archive, comment);

        /*
         * 读压缩文件，注释掉，因为使用的是apache的压缩类，所以使用java类库中
         * 解压类时出错，这里不能运行
         */
        //readByZipInputStream();
        //----使用apace ZipFile读取压缩文件
//        readByApacheZipFile(archive, decompressDir);
    }

    public static void writeByApacheZipOutputStream(String src, String archive, String comment) throws IOException {
        //----压缩文件：
        FileOutputStream f = new FileOutputStream(archive);
        //使用指定校验和创建输出流
        CheckedOutputStream csum = new CheckedOutputStream(f, new CRC32());

        ZipOutputStream zos = new ZipOutputStream(csum);
        BufferedOutputStream out = new BufferedOutputStream(zos);
        //设置压缩包注释
        zos.setComment(comment);
        //启用压缩
        zos.setMethod(ZipOutputStream.DEFLATED);
        //压缩级别为最强压缩，但时间要花得多一点
        zos.setLevel(Deflater.BEST_COMPRESSION);

        File srcFile = new File(src);

        if (!srcFile.exists() || (srcFile.isDirectory() && srcFile.list().length == 0)) {
            throw new FileNotFoundException(
                    "File must exist and  ZIP file must have at least one entry.");
        }
        //获取压缩源所在父目录
        src = src.replaceAll("\\\\", "/");
        String prefixDir = null;
        if (srcFile.isFile()) {
            prefixDir = src.substring(0, src.lastIndexOf("/") + 1);
        } else {
            prefixDir = (src.replaceAll("/$", "") + "/");
        }

        //如果不是根目录
        if (prefixDir.indexOf("/") != (prefixDir.length() - 1) && isCreateSrcDir) {
            prefixDir = prefixDir.replaceAll("[^/]+/$", "");
        }

        //开始压缩
        writeRecursive(zos, out, srcFile, prefixDir);

        out.close();
        // 注：校验和要在流关闭后才准备，一定要放在流被关闭后使用
        System.out.println("Checksum: " + csum.getChecksum().getValue());
    }

    /**
     * 使用 org.apache.tools.zip.ZipFile 解压文件，它与 java 类库中的
     * java.util.zip.ZipFile 使用方式是一样的，只不过多了设置编码方式的
     * 接口。
     *
     * 注，apache 没有提供 ZipInputStream 类，所以只能使用它提供的ZipFile
     * 来读取压缩文件。
     * @param archive 压缩包路径
     * @param decompressDir 解压路径
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ZipException
     */
    public static void readByApacheZipFile(String archive, String decompressDir)
            throws IOException, FileNotFoundException, ZipException {
        BufferedInputStream bi;

        ZipFile zf = new ZipFile(archive);
        Enumeration e = zf.entries();
        while (e.hasMoreElements()) {
            ZipEntry ze2 = (ZipEntry) e.nextElement();
            String entryName = ze2.getName();
            String path = decompressDir + "/" + entryName;
            if (ze2.isDirectory()) {
                System.out.println("正在创建解压目录 - " + entryName);
                File decompressDirFile = new File(path);
                if (!decompressDirFile.exists()) {
                    decompressDirFile.mkdirs();
                }
            } else {
                System.out.println("正在创建解压文件 - " + entryName);
                String fileDir = path.substring(0, path.lastIndexOf("/"));
                File fileDirFile = new File(fileDir);
                if (!fileDirFile.exists()) {
                    fileDirFile.mkdirs();
                }
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(
                        decompressDir + "/" + entryName));

                bi = new BufferedInputStream(zf.getInputStream(ze2));
                byte[] readContent = new byte[1024];
                int readCount = bi.read(readContent);
                while (readCount != -1) {
                    bos.write(readContent, 0, readCount);
                    readCount = bi.read(readContent);
                }
                bos.close();
            }
        }
        zf.close();
    }

    /**
     * 使用 java api 中的 ZipInputStream 类解压文件，但如果压缩时采用了
     * org.apache.tools.zip.ZipOutputStream时，而不是 java 类库中的
     * java.util.zip.ZipOutputStream时，该方法不能使用，原因就是编码方
     * 式不一致导致，运行时会抛如下异常：
     * java.lang.IllegalArgumentException
     * at java.util.zip.ZipInputStream.getUTF8String(ZipInputStream.java:290)
     *
     * 当然，如果压缩包使用的是java类库的java.util.zip.ZipOutputStream
     * 压缩而成是不会有问题的，但它不支持中文
     *
     * @param archive 压缩包路径
     * @param decompressDir 解压路径
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void readByZipInputStream(String archive, String decompressDir)
            throws FileNotFoundException, IOException {
        BufferedInputStream bi;
        //----解压文件(ZIP文件的解压缩实质上就是从输入流中读取数据):
        System.out.println("开始读压缩文件");

        FileInputStream fi = new FileInputStream(archive);
        CheckedInputStream csumi = new CheckedInputStream(fi, new CRC32());
        ZipInputStream in2 = new ZipInputStream(csumi);
        bi = new BufferedInputStream(in2);
        java.util.zip.ZipEntry ze;//压缩文件条目
        //遍历压缩包中的文件条目
        while ((ze = in2.getNextEntry()) != null) {
            String entryName = ze.getName();
            if (ze.isDirectory()) {
                System.out.println("正在创建解压目录 - " + entryName);
                File decompressDirFile = new File(decompressDir + "/" + entryName);
                if (!decompressDirFile.exists()) {
                    decompressDirFile.mkdirs();
                }
            } else {
                System.out.println("正在创建解压文件 - " + entryName);
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(
                        decompressDir + "/" + entryName));
                byte[] buffer = new byte[1024];
                int readCount = bi.read(buffer);

                while (readCount != -1) {
                    bos.write(buffer, 0, readCount);
                    readCount = bi.read(buffer);
                }
                bos.close();
            }
        }
        bi.close();
        System.out.println("Checksum: " + csumi.getChecksum().getValue());
    }

    /**
     * 递归压缩
     *
     * 使用 org.apache.tools.zip.ZipOutputStream 类进行压缩，它的好处就是支持中文路径，
     * 而Java类库中的 java.util.zip.ZipOutputStream 压缩中文文件名时压缩包会出现乱码。
     * 使用 apache 中的这个类与 java 类库中的用法是一新的，只是能设置编码方式了。
     *
     * @param zos
     * @param bo
     * @param srcFile
     * @param prefixDir
     * @throws IOException
     * @throws FileNotFoundException
     */
    private static void writeRecursive(ZipOutputStream zos, BufferedOutputStream bo,
                                       File srcFile, String prefixDir) throws IOException, FileNotFoundException {
        ZipEntry zipEntry;

        String filePath = srcFile.getAbsolutePath().replaceAll("\\\\", "/").replaceAll(
                "//", "/");
        if (srcFile.isDirectory()) {
            filePath = filePath.replaceAll("/$", "") + "/";
        }
        String entryName = filePath.replace(prefixDir, "").replaceAll("/$", "");
        if (srcFile.isDirectory()) {
            if (!"".equals(entryName)) {
                System.out.println("正在创建目录 - " + srcFile.getAbsolutePath()
                        + "  entryName=" + entryName);

                //如果是目录，则需要在写目录后面加上 /
                zipEntry = new ZipEntry(entryName + "/");
                zos.putNextEntry(zipEntry);
            }

            File srcFiles[] = srcFile.listFiles();
            for (int i = 0; i < srcFiles.length; i++) {
                writeRecursive(zos, bo, srcFiles[i], prefixDir);
            }
        } else {
            System.out.println("正在写文件 - " + srcFile.getAbsolutePath() + "  entryName="
                    + entryName);
            BufferedInputStream bi = new BufferedInputStream(new FileInputStream(srcFile));

            //开始写入新的ZIP文件条目并将流定位到条目数据的开始处
            zipEntry = new ZipEntry(entryName);
            zos.putNextEntry(zipEntry);
            byte[] buffer = new byte[1024];
            int readCount = bi.read(buffer);

            while (readCount != -1) {
                bo.write(buffer, 0, readCount);
                readCount = bi.read(buffer);
            }
            //注，在使用缓冲流写压缩文件时，一个条件完后一定要刷新一把，不
            //然可能有的内容就会存入到后面条目中去了
            bo.flush();
            //文件读完后关闭
            bi.close();
        }
    }
}
