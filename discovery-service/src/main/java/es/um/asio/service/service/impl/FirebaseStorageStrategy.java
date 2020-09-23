package es.um.asio.service.service.impl;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.*;
import org.mariadb.jdbc.internal.logging.Logger;
import org.mariadb.jdbc.internal.logging.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Date;

@Service
public class FirebaseStorageStrategy{

    private final Logger log = LoggerFactory.getLogger(FirebaseStorageStrategy.class);
    private StorageOptions storageOptions;
    private static String PROJECT_ID = "discovery-lib";
    private static String BUCKET_NAME = "discovery-lib.appspot.com";

    @PostConstruct
    private void initializeFirebase() throws Exception {
        URI uri = getClass().getClassLoader().getResource("discovery-lib-firebase-adminsdk-93pb4-46e5934f16.json").toURI();
        FileInputStream serviceAccount = new FileInputStream(uri.getPath());
        this.storageOptions = StorageOptions.newBuilder()
                .setProjectId(PROJECT_ID)
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
    }

    public String readFileFromStorage(String fileName){
        Storage storage = storageOptions.getService();
        Blob blob = storage.get(BUCKET_NAME, fileName);
        String fileContent = new String(blob.getContent());
        return fileContent;
    }

    public String[] writeFile(String fileName, String content) {
        Storage storage = storageOptions.getService();
        BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        Blob blob = storage.create(blobInfo, content.getBytes());
        log.info("File " + fileName + " uploaded to bucket " + BUCKET_NAME + " as " + fileName);
        return new String[]{"fileUrl", fileName};
    }

}
