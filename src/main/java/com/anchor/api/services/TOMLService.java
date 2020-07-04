package com.anchor.api.services;

import com.anchor.api.util.Emoji;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moandjiezana.toml.Toml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Logger;

@Service
public class TOMLService {
    public static final Logger LOGGER = Logger.getLogger(TOMLService.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    @Autowired
    private CryptoService cryptoService;

    @Value("${tomlObject}")
    private String tomlObject;

    @Value("${projectId}")
    private String projectId;

    @Value("${bucketName}")
    private String bucketName;

    @Value("${locationId}")
    private String locationId;

    @Value("${keyRingId}")
    private String keyRingId;

    @Value("${cryptoKeyId}")
    private String cryptoKeyId;

    private Toml anchorToml, stellarToml;

    public TOMLService() {
        LOGGER.info(Emoji.DOG.concat(Emoji.DOG)
        .concat("Service for TOML up and running \uD83D\uDE21"));
    }

    private static final String ANCHOR_TOML = "ANCHOR_TOML_", STELLAR_TOML = "STELLAR_TOML_";
    public void encryptAndUploadAnchorFile(String anchorId, File file) throws IOException {
        LOGGER.info(Emoji.HASH.concat(Emoji.HASH).concat(Emoji.HASH).concat("encryptAndUploadAnchorFile for ".concat(anchorId)));
        byte[] bytes = getBytes(file);
        byte[] encrypted = cryptoService.encrypt(ANCHOR_TOML + anchorId, new String(bytes));
        LOGGER.info(Emoji.SKULL.concat(Emoji.SKULL.concat("ANCHOR_TOML File has been encrypted and uploaded to cloud storage\n"
               )));
    }
    public void encryptAndUploadStellarFile(String anchorId, File file) throws IOException {
        LOGGER.info(Emoji.HASH.concat(Emoji.HASH).concat(Emoji.HASH).concat("encryptAndUploadStellarFile for ".concat(anchorId)));
        byte[] bytes = getBytes(file);
        byte[] encrypted = cryptoService.encrypt(STELLAR_TOML + anchorId, new String(bytes));
        LOGGER.info(Emoji.SKULL.concat(Emoji.SKULL.concat("STELLAR_TOML File has been encrypted  and uploaded to cloud storage\n"
                )));
    }

    private byte[] getBytes(File file) throws IOException {
        Path path = Paths.get(file.getAbsolutePath());
        return Files.readAllBytes(path);
    }


    public Toml getStellarToml(String anchorId) throws Exception {
        if (stellarToml != null) {
            return stellarToml;
        }
        try {
            String data = cryptoService.getDecryptedSeed(STELLAR_TOML + anchorId);
            LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF).concat("data: ".concat(data)));
            Path mPath = Files.write(Paths.get(DOWNLOAD_PATH.concat(anchorId)), data.getBytes());
            stellarToml = new Toml().read(mPath.toFile());
            LOGGER.info(Emoji.PRESCRIPTION + Emoji.PRESCRIPTION + Emoji.PRESCRIPTION + "....... stellar.toml file found from encrypted storage.");
            LOGGER.info(stellarToml.toString());
        } catch (Exception e) {
            LOGGER.info(Emoji.PEPPER + Emoji.PEPPER + Emoji.PEPPER + "....... Failed to get stellar.toml file from encrypted storage." +
                    " solve this by uploading anchor.toml via AnchorController.uploadTOML \uD83D\uDE21 \uD83D\uDE21 \uD83D\uDE21 ");
        }
        return stellarToml;
    }
    public Toml getAnchorToml(String anchorId) throws Exception {
        if (anchorToml != null) {
            return anchorToml;
        }
        try {
            String data = cryptoService.getDecryptedSeed(ANCHOR_TOML + anchorId);
            LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF).concat("data: ".concat(data)));
            Path mPath = Files.write(Paths.get(DOWNLOAD_PATH.concat(anchorId)), data.getBytes());
            anchorToml = new Toml().read(mPath.toFile());
            LOGGER.info(Emoji.PRESCRIPTION + Emoji.PRESCRIPTION + Emoji.PRESCRIPTION + "....... anchor.toml file found from encrypted storage.");
            LOGGER.info(anchorToml.toString());
        } catch (Exception e) {
            LOGGER.info(Emoji.PEPPER + Emoji.PEPPER + Emoji.PEPPER + "....... Failed to get anchor.toml file from encrypted storage." +
                    " solve this by uploading anchor.toml via AnchorController.uploadTOML \uD83D\uDE21 \uD83D\uDE21 \uD83D\uDE21 ");
        }
        return anchorToml;
    }
    public static final String DOWNLOAD_PATH = "downloaded_toml", UPLOAD_PATH = "upload_toml";

//
//    public void uploadStellarTOMLFile(String anchorId) throws IOException {
//        File file = new File(UPLOAD_PATH.concat(STELLAR_TOML + anchorId));
//        Storage storage = StorageOptions.newBuilder().setProjectId(projectId)
//                .build().getService();
//        BlobId blobId = BlobId.of(bucketName, STELLAR_TOML + anchorId);
//        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
//
//        storage.create(blobInfo, Files.readAllBytes(Paths.get(file.getAbsolutePath())));
//        Files.delete(Paths.get(file.getAbsolutePath()));
//        LOGGER.info(
//                "... \uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 Yebo!! \uD83C\uDF4E " +
//                        "Encrypted STELLAR TOML File " + file.getAbsolutePath() + " uploaded to \uD83C\uDF3C " +
//                        "bucket " + bucketName + " \uD83C\uDF3C as " + tomlObject);
//    }
//    public void uploadAnchorTOMLFile(String anchorId) throws IOException {
//        File file = new File(UPLOAD_PATH.concat(ANCHOR_TOML + anchorId));
//        Storage storage = StorageOptions.newBuilder().setProjectId(projectId)
//                .build().getService();
//        BlobId blobId = BlobId.of(bucketName, ANCHOR_TOML + anchorId);
//        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
//
//        storage.create(blobInfo, Files.readAllBytes(Paths.get(file.getAbsolutePath())));
//        Files.delete(Paths.get(file.getAbsolutePath()));
//        LOGGER.info(
//                "... \uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 Yebo!! \uD83C\uDF4E " +
//                        "Encrypted ANCHOR TOML File " + file.getAbsolutePath() + " uploaded to \uD83C\uDF3C " +
//                        "bucket " + bucketName + " \uD83C\uDF3C as " + tomlObject);
//    }
}
