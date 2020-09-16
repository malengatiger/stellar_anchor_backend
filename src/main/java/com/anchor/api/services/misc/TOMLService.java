package com.anchor.api.services.misc;

import com.anchor.api.util.E;
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
import java.util.Date;
import java.util.logging.Logger;

@Service
public class TOMLService {
    public static final Logger LOGGER = Logger.getLogger(TOMLService.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    @Autowired
    private CryptoService cryptoService;

    @Value("${tomlObject}")
    private String tomlObject;

    @Value("${stellarProjectId}")
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
        LOGGER.info(E.DOG.concat(E.DOG)
        .concat("Service for TOML up and running \uD83D\uDE21"));
    }

    public static final String ANCHOR_TOML = "ANCHOR_TOML_FILE", STELLAR_TOML = "STELLAR_TOML_FILE";
    public void encryptAndUploadAnchorFile(File file) throws IOException {
        LOGGER.info(E.HASH.concat(E.HASH).concat(E.HASH).concat("encryptAndUploadAnchorFile for Anchor"));
        byte[] bytes = getBytes(file);
        cryptoService.encrypt(ANCHOR_TOML, new String(bytes));
        LOGGER.info(E.SKULL.concat(E.SKULL.concat("ANCHOR_TOML File has been encrypted and uploaded to cloud storage" +
                " from ".concat(file.getAbsolutePath())
               )));
        boolean deleted = file.delete();
        LOGGER.info(E.SKULL.concat(E.SKULL.concat("temporary File has been deleted: " + deleted + " \uD83D\uDC4C\uD83C\uDFFE")));
    }
    public void encryptAndUploadStellarFile(File file) throws IOException {
        LOGGER.info(E.HASH.concat(E.HASH).concat(E.HASH).concat("encryptAndUploadStellarFile for Anchor"));
        byte[] bytes = getBytes(file);
        cryptoService.encrypt(STELLAR_TOML, new String(bytes));
        LOGGER.info(E.SKULL.concat(E.SKULL.concat("STELLAR_TOML File has been encrypted  and uploaded to cloud storage" +
                        " from ".concat(file.getAbsolutePath())
                )));
        boolean deleted = file.delete();
        LOGGER.info(E.SKULL.concat(E.SKULL.concat("temporary File has been deleted: " + deleted + " \uD83D\uDC4C\uD83C\uDFFE")));
    }

    private byte[] getBytes(File file) throws IOException {
        Path path = Paths.get(file.getAbsolutePath());
        return Files.readAllBytes(path);
    }


    public Toml getStellarToml() {
        if (stellarToml != null) {
            return stellarToml;
        }
        LOGGER.info(E.PEPPER + E.PEPPER + E.PEPPER + "TOMLService getting Stellar toml file ..... ");
        try {
            String data = cryptoService.getDecryptedSeed(STELLAR_TOML);
            LOGGER.info(E.LEAF.concat(E.LEAF).concat("data: ".concat(data)));
            Path mPath = Files.write(Paths.get(DOWNLOAD_PATH.concat(new Date().toString())), data.getBytes());
            stellarToml = new Toml().read(mPath.toFile());
            LOGGER.info(E.PRESCRIPTION + E.PRESCRIPTION + E.PRESCRIPTION + "....... stellar.toml file found from encrypted storage.");
            boolean isDeleted = mPath.toFile().delete();
            LOGGER.info(E.BELL + E.BELL + E.BELL + "....... temporary download file deleted: " + isDeleted);
            LOGGER.info(stellarToml.toString());
        } catch (Exception e) {
            LOGGER.info(E.PEPPER + E.PEPPER + E.PEPPER + "....... Failed to get stellar.toml file from encrypted storage." +
                    " solve this by uploading anchor.toml via AnchorController.uploadTOML \uD83D\uDE21 \uD83D\uDE21 \uD83D\uDE21 ");
        }
        return stellarToml;
    }
    public Toml getAnchorToml() {
        if (anchorToml != null) {
            return anchorToml;
        }
        try {
            String data = cryptoService.getDecryptedSeed(ANCHOR_TOML);
            LOGGER.info(E.LEAF.concat(E.LEAF).concat((".............." +
                    "anchor.toml decrypted data: ").concat(data)));

            Path mPath = Files.write(Paths.get(DOWNLOAD_PATH.concat("" + new Date().getTime())), data.getBytes());
            LOGGER.info(E.LEAF.concat(E.LEAF).concat((".............. path to downloaded file: " + mPath.toAbsolutePath().toString())));
            anchorToml = new Toml().read(mPath.toFile());
            LOGGER.info(E.PRESCRIPTION + E.PRESCRIPTION + E.PRESCRIPTION + "....... anchor.toml file found from encrypted storage.");
            boolean isDeleted = mPath.toFile().delete();
            LOGGER.info(E.PRESCRIPTION + E.PRESCRIPTION + E.PRESCRIPTION + "....... temporary download file deleted: " + isDeleted);
            LOGGER.info(anchorToml.toString());
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info(E.PEPPER + E.PEPPER + E.PEPPER + "....... Failed to get anchor.toml file from encrypted storage." +
                    " solve this by uploading anchor.toml via AnchorController.uploadTOML \uD83D\uDE21 \uD83D\uDE21 \uD83D\uDE21 ");
        }
        return anchorToml;
    }
    public static final String DOWNLOAD_PATH = "downloaded_toml", UPLOAD_PATH = "upload_toml";

}
