package com.telegram.social_poster.Services;

import lombok.SneakyThrows;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class FileService {

    @SneakyThrows
    public void downloadAndSaveVideo(String url, String savePath) {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);

        HttpResponse response = httpClient.execute(httpGet);

        if (response.getStatusLine().getStatusCode() == 200) {
            try (InputStream inStream = response.getEntity().getContent();
                 FileOutputStream outStream = new FileOutputStream(savePath)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
            }

            System.out.println("File downloaded successfully.");
        } else {
            System.out.println("Failed to download the file. HTTP status code: " + response.getStatusLine().getStatusCode());
        }
        httpClient.close();
    }
}
