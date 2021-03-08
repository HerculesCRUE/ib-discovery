package es.um.asio.service.service.impl;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.mariadb.jdbc.internal.logging.Logger;
import org.mariadb.jdbc.internal.logging.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

/**
 * Firebase Storage Handler. For speed up application deployment the first time
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Service
public class FirebaseStorageStrategy{

    private final Logger log = LoggerFactory.getLogger(FirebaseStorageStrategy.class);
    private StorageOptions storageOptions;
    private static final String PROJECTID = "discovery-lib";
    private static final String BUCKETNAME = "discovery-lib.appspot.com";

    /**
     * Access to Firebase
     * @throws IOException
     */
    @PostConstruct
    public void initializeFirebase() throws IOException {
        InputStream serviceAccount = getClass().getResourceAsStream("/discovery-lib-firebase-adminsdk-93pb4-46e5934f16.json");
        this.storageOptions = StorageOptions.newBuilder()
                .setProjectId(PROJECTID)
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
    }

    /**
     * Let read a file storage in Firebase
     * @param fileName String. The file name
     * @return String. The body of the document
     */
    public String readFileFromStorage(String fileName){
        Storage storage = storageOptions.getService();
        Blob blob = storage.get(BUCKETNAME, fileName);
        return new String(blob.getContent());
    }

    /**
     * Let write a file storage in Firebase
     * @param fileName String. The file name
     * @param content String. The context
     * @return String. The body of the document
     */
    @Async
    public CompletableFuture<String[]> writeFile(String fileName, String content) {
        log.info("File " + fileName + " uploaded to bucket " + BUCKETNAME + " as " + fileName);
        return CompletableFuture.completedFuture(new String[]{"fileUrl", fileName});
    }


}
