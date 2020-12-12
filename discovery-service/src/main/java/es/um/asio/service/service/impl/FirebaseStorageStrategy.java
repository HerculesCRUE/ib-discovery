package es.um.asio.service.service.impl;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.mariadb.jdbc.internal.logging.Logger;
import org.mariadb.jdbc.internal.logging.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;


@Service
public class FirebaseStorageStrategy{

    private final Logger log = LoggerFactory.getLogger(FirebaseStorageStrategy.class);
    private StorageOptions storageOptions;
    private static String PROJECT_ID = "discovery-lib";
    private static String BUCKET_NAME = "discovery-lib.appspot.com";

    @PostConstruct
    public void initializeFirebase() throws IOException {
        InputStream serviceAccount = getClass().getResourceAsStream("/discovery-lib-firebase-adminsdk-93pb4-46e5934f16.json");
        this.storageOptions = StorageOptions.newBuilder()
                .setProjectId(PROJECT_ID)
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
    }

    public String readFileFromStorage(String fileName){
        Storage storage = storageOptions.getService();
        Blob blob = storage.get(BUCKET_NAME, fileName);
        return new String(blob.getContent());
    }

    @Async
    public CompletableFuture<String[]> writeFile(String fileName, String content) {
        BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
        log.info("File " + fileName + " uploaded to bucket " + BUCKET_NAME + " as " + fileName);
        return CompletableFuture.completedFuture(new String[]{"fileUrl", fileName});
    }


}
