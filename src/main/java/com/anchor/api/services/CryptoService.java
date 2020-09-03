package com.anchor.api.services;

import com.anchor.api.util.E;
import com.google.cloud.kms.v1.*;
import com.google.cloud.storage.*;
import com.google.protobuf.ByteString;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class CryptoService {
    public CryptoService() {
        LOGGER.info("\uD83D\uDD11 \uD83D\uDD11 CryptoService; \uD83D\uDD11 ... to hide shit ... \uD83D\uDD11");
    }

    private KeyManagementServiceClient client;
    public static final Logger LOGGER = LoggerFactory.getLogger(CryptoService.class);
    private void listKeyRings() throws IOException {
        // Create the KeyManagementServiceClient using try-with-resources to manage client cleanup.
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            // The resource name of the location to search
            String locationPath = LocationName.format(projectId, locationId);
            // Make the RPC call
            KeyManagementServiceClient.ListKeyRingsPagedResponse response = client.listKeyRings(locationPath);
            // Iterate over all KeyRings (which may cause more result pages to be loaded automatically)
            for (KeyRing keyRing : response.iterateAll()) {
                LOGGER.info("\uD83C\uDF4F \uD83C\uDF4E Found KMS KeyRing: " + keyRing.getName());
            }
        }
    }


    @Value("${locationId}")
    private String locationId;
    @Value("${stellarProjectId}")
    private String projectId;
    @Value("${stellarKeyRingId}")
    private String keyRingId;
    @Value("${stellarCryptoKeyId}")
    private String cryptoKeyId;

    /**
     *  Creates a new key ring with the given id
     * @return string
     * @throws Exception error
     */
    public String createKeyRing()
            throws Exception {
        LOGGER.info("\uD83D\uDD11 \uD83D\uDD11 \uD83D\uDD11 createKeyRing: ".concat(keyRingId));
        listKeyRings();
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            // The resource name of the location associated with the KeyRing.
            String parent = LocationName.format(projectId, locationId);
            // Create the KeyRing for your project.
            KeyRing keyRing = client.createKeyRing(parent, keyRingId, KeyRing.newBuilder().build());
            LOGGER.info("\uD83C\uDF4E \uD83C\uDF4E KeyRing created: ".concat(keyRing.getName()));

            return keyRing.getName();
        }
    }

    /** Creates a new crypto key with the given id. */
    public String createCryptoKey()
            throws IOException {
        // Create the Cloud KMS client.
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            // The resource name of the location associated with the KeyRing.
            String parent = KeyRingName.format(projectId, locationId, keyRingId);
            // This will allow the API access to the key for encryption and decryption.
            CryptoKey cryptoKey =
                    CryptoKey.newBuilder().setPurpose(CryptoKey.CryptoKeyPurpose.ENCRYPT_DECRYPT).build();
            // Create the CryptoKey for your project.
            CryptoKey createdKey = client.createCryptoKey(parent, cryptoKeyId, cryptoKey);
            LOGGER.info("\uD83C\uDF4E \uD83C\uDF4E CryptoKey created: ".concat(createdKey.getName()));
            return createdKey.getName();
        }
    }

    /** Encrypts the given plaintext using the specified crypto key. */
    public byte[] encrypt(String id, String stringToEncrypt)
            throws IOException {

        byte[] stringToEncryptBytes = stringToEncrypt.getBytes();
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            String resourceName = CryptoKeyPathName.format(projectId, locationId, keyRingId, cryptoKeyId);
            EncryptResponse response = client.encrypt(resourceName, ByteString.copyFrom(stringToEncryptBytes));
            writeFile(id,response.getCiphertext().toByteArray());
            uploadFile(id);

            return response.getCiphertext().toByteArray();
        }
    }

    /** Decrypts the provided ciphertext with the specified crypto key. */
    public String decrypt(byte[] encryptedStringBytes )
            throws IOException {
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            String resourceName = CryptoKeyPathName.format(projectId, locationId, keyRingId, cryptoKeyId);
            DecryptResponse response = client.decrypt(resourceName, ByteString.copyFrom(encryptedStringBytes));
            return response.getPlaintext().toStringUtf8();
        }
    }

    @Value("${stellarBucketName}")
    private String bucketName;
    @Value("${objectName}")
    private String objectName;
    public static final String FILE_PATH = "encrypted_seed";


    public void writeFile(String accountId, byte[] encryptedSeed)
            throws IOException {

        Path path = Paths.get(FILE_PATH.concat(accountId));
        Files.write(path, encryptedSeed);

    }
    public byte[] readFile(String id)
            throws IOException {

        Path path = Paths.get(DOWNLOAD_PATH.concat(id));
        byte[] read = Files.readAllBytes(path);
        Files.delete(path);
        return read;
    }

    public void uploadFile(String id) throws IOException {
        LOGGER.info(
                "... \uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 Uploading file \uD83C\uDF4E bucketName: " + bucketName + " id: " + id
        + " projectId: " + projectId);
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId)
                .build().getService();
        BlobId blobId = BlobId.of(bucketName, id);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        storage.create(blobInfo, Files.readAllBytes(Paths.get(FILE_PATH.concat(id))));
        File gg = new File(String.valueOf(Paths.get(FILE_PATH.concat(id))));
        if (gg.exists()) {
            boolean deleted = gg.delete();
            LOGGER.info(E.PRESCRIPTION + E.PRESCRIPTION + E.PRESCRIPTION
                    + ".......temporary file deleted: " + deleted);
        }

        LOGGER.info(
                "... \uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 Yebo!! \uD83C\uDF4E " +
                        "Encrypted Seed File \uD83C\uDF4E \uD83C\uDF4E " + FILE_PATH.concat(id) + " \uD83C\uDF4E \uD83C\uDF4E uploaded to \uD83C\uDF3C " +
                        "bucketName: " + bucketName + " \uD83C\uDF3C as object: " + id);
    }
    public static final String DOWNLOAD_PATH = "downloaded_seed";

    public String getDecryptedSeed(String accountId) throws Exception {
        downloadSeedFile(accountId);
        byte[] bytes = readFile(accountId);
        return decrypt(bytes);
    }

    private void downloadSeedFile(String accountId) throws Exception {
        LOGGER.info(E.YELLOW_BIRD.concat(E.YELLOW_BIRD).concat(" .... about to download seed file for accountId: "
                .concat(accountId).concat(" bucketName: ").concat(bucketName).concat( " " + E.RED_APPLE
                .concat(" projectId: ".concat(projectId)))
        .concat(" object: ".concat(objectName)).concat(" ").concat(E.RED_APPLE)));
        String name = getPath(accountId);
        Path destFilePath = Paths.get(DOWNLOAD_PATH.concat(accountId));
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        LOGGER.info(E.YELLOW_BIRD.concat(E.YELLOW_BIRD)
                .concat(" serviceAccount email: ")
                .concat(storage.getServiceAccount(projectId).getEmail()));
        Blob blob = storage.get(BlobId.of(bucketName, accountId));
        if (blob == null) {
            LOGGER.info(E.NOT_OK.concat(E.NOT_OK).concat(E.PEPPER).concat("Blob for downloading is fucking NULL? \uD83D\uDE21 WTF? \uD83D\uDE21 name: "
            .concat(accountId).concat(" in bucket: ".concat(bucketName))));
            throw new Exception(E.NOT_OK + "KMS Blob for downloading is fucking NULL? WTF?");
        }
        blob.downloadTo(destFilePath);
        LOGGER.info(E.YELLOW_BIRD.concat(E.YELLOW_BIRD)
                .concat(" seed file has been downloaded OK into: ".concat(destFilePath.toString()))
                .concat(destFilePath.toAbsolutePath().toString()));
    }

    @NotNull
    private String getPath(String anchorId) throws Exception {
        File dir = new File("seed");
        if (!dir.exists()) {
            boolean ok = dir.mkdir();
            if (!ok) {
                LOGGER.info(E.ERROR+ E.ERROR+ E.ERROR+" Unable to create directory");
                throw new Exception("Unable to create directory");
            }
        }
        return dir.getAbsolutePath().concat("/").concat(DOWNLOAD_PATH).concat(anchorId);
    }
}
