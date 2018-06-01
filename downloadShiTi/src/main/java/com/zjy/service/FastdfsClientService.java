package com.zjy.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

import java.io.*;
import java.net.URL;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2018/6/1.
 */
public class FastdfsClientService {
    private static final String CONFIG_FILENAME = "/fdfs_client.conf";

    private static StorageClient1 storageClient1 = null;

    // 初始化FastDFS Client
    static {
        try {
            ClientGlobal.init(CONFIG_FILENAME);
            TrackerClient trackerClient = new TrackerClient(ClientGlobal.g_tracker_group);
            TrackerServer trackerServer = trackerClient.getConnection();
            if (trackerServer == null) {
                throw new IllegalStateException("getConnection return null");
            }

            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            if (storageServer == null) {
                throw new IllegalStateException("getStoreStorage return null");
            }

            storageClient1 = new StorageClient1(trackerServer, storageServer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传文件
     *
     * @param file     文件对象
     * @return
     */
    public static String uploadFile(File file) {
        return uploadFile(file, null);
    }

    /**
     * 上传文件
     *
     * @param file     文件对象
     * @param metaList 文件元数据
     * @return
     */
    public static String uploadFile(File file, Map<String, String> metaList) {
        try {
            byte[] buff = org.apache.commons.io.IOUtils.toByteArray(new FileInputStream(file));
            NameValuePair[] nameValuePairs = null;
            if (metaList != null) {
                nameValuePairs = new NameValuePair[metaList.size()];
                int index = 0;
                for (Iterator<Map.Entry<String, String>> iterator = metaList.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<String, String> entry = iterator.next();
                    String name = entry.getKey();
                    String value = entry.getValue();
                    nameValuePairs[index++] = new NameValuePair(name, value);
                }
            }
            String extension = FilenameUtils.getExtension(file.getName());
            return storageClient1.upload_file1(buff, extension, nameValuePairs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文件元数据
     *
     * @param fileId 文件ID
     * @return
     */
    public static Map<String, String> getFileMetadata(String fileId) {
        try {
            NameValuePair[] metaList = storageClient1.get_metadata1(fileId);
            if (metaList != null) {
                HashMap<String, String> map = new HashMap<String, String>();
                for (NameValuePair metaItem : metaList) {
                    map.put(metaItem.getName(), metaItem.getValue());
                }
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @return 删除失败返回-1，否则返回0
     */
    public static int deleteFile(String fileId) {
        try {
            return storageClient1.delete_file1(fileId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 下载文件
     *
     * @param fileId  文件ID（上传文件成功后返回的ID）
     * @param outFile 文件下载保存位置
     * @return
     */
    public static int downloadFile(String fileId, File outFile) {
        FileOutputStream fos = null;
        try {
            getUrl1(fileId);
            byte[] content = storageClient1.download_file1(fileId);
            if(content == null) {
                System.out.println("length is 0");
            }
            fos = new FileOutputStream(outFile);
            FileUtils.writeByteArrayToFile(outFile, content);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }

    public static String getUrl1(String path) {
        // unix seconds
        int ts = (int) Instant.now().getEpochSecond();
        // token
        String token = "";
        try {
            System.out.printf(ClientGlobal.g_secret_key);
            token = ProtoCommon.getToken(getFilename(path), ts, ClientGlobal.g_secret_key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("token=").append(token);
        sb.append("&ts=").append(ts);
        return sb.toString();
    }

    public static String getFilename(String fileId){
        String[] results = new String[2];
        StorageClient1.split_file_id(fileId, results);
        System.out.println("fileName:" + results[1]);
        return results[1];
    }

    public static String testUpload() {
        File file = new File("/home/zjy/software/test.png");
        Map<String, String> metaList = new HashMap<String, String>();
        metaList.put("width", "1024");
        metaList.put("height", "768");
        metaList.put("author", "杨信");
        metaList.put("date", "20161018");
        String fid = uploadFile(file, metaList);
        System.out.println("upload local file " + file.getPath() + " ok, fileid=" + fid);
        //上传成功返回的文件ID： group1/M00/00/00/wKgAyVgFk9aAB8hwAA-8Q6_7tHw351.jpg
        return fid;
    }

    /**
     * 文件下载测试
     */
    public static void testDownload(String fid) {
        int r = downloadFile(fid, new File("/home/zjy/software/test_new.png"));
        System.out.println(r == 0 ? "下载成功" : "下载失败");
    }
}
