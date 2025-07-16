package com.sideproject.myshop.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// 把前端收到的檔案，透過 HTTP PUT 請求上傳到指定的遠端伺服器
@Service
public class FileUploadService {

    @Value("${FILE_ZONE}")
    private String storageZone;

    @Value("${FILE_UPLOAD_API_KEY}")
    private String fileUploadKey;

    @Value("${FILE_UPLOAD_HOST_URL}")
    private String fileHostName;


    public int uploadFile(MultipartFile file,String fileName){

        try {
            String urlString =  fileHostName+"/"+storageZone+"/"+fileName;
            URL url = new URL(urlString);

            // 建立一個 PUT 請求連線
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 設定 HTTP 請求屬性
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("AccessKey",fileUploadKey);
            connection.setRequestProperty("Content-Type", "application/octet-stream");
            connection.setDoOutput(true);


            long fileSize = file.getSize();

            // 從 MultipartFile 取得輸入流，並取得 HTTP 連線的輸出流，準備把檔案寫入。
            try (BufferedInputStream inputStream = new BufferedInputStream(file.getInputStream());
                 BufferedOutputStream outputStream = new BufferedOutputStream(connection.getOutputStream())) {

                // 用 8192 位元組（8KB）為單位，一塊一塊把檔案傳給 BunnyCDN。
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            //  取得上傳結果
            int responseCode = connection.getResponseCode();
            String responseMsg = connection.getResponseMessage();
            return responseCode;
        }
        catch (Exception e){
            return 500;
        }
    }
}

