package com.anchor.api.controllers;

import com.anchor.api.data.anchor.Client;
import com.anchor.api.services.AgentService;
import com.anchor.api.services.FileService;
import com.anchor.api.services.FirebaseService;
import com.anchor.api.services.TOMLService;
import com.anchor.api.util.E;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

@RestController
@CrossOrigin(maxAge = 3600)
public class ClientController {
    public static final Logger LOGGER = Logger.getLogger(ClientController.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public ClientController() {
        LOGGER.info(E.BURGER.concat(E.BURGER.concat("ClientController waiting to go! ").concat(E.CHICKEN)));
    }

    @Autowired
    private FileService fileService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private TOMLService tomlService;

    @Autowired
    private FirebaseService firebaseService;

    @PostMapping(value = "/registerClient", produces = MediaType.APPLICATION_JSON_VALUE)
    public Client registerClient(@RequestBody Client client) throws Exception {
        LOGGER.info(em + " ClientController:registerClient ...");
        if (client.getAnchorId() == null) {
            throw new Exception("Anchor missing");
        }

        Client mClient = agentService.createClient(client);
        LOGGER.info(E.LEAF.concat(E.LEAF) + "AgentService returns Client with brand new Stellar account: \uD83C\uDF4E "
                + mClient.getFullName());
        return mClient;

    }

    @PostMapping(value = "/updateClient", produces = MediaType.TEXT_PLAIN_VALUE)
    public String updateClient(@RequestBody Client client) throws Exception {
        LOGGER.info(E.RAIN_DROPS.concat(E.RAIN_DROPS) + "AnchorController:updateClient...");
        String mClient = agentService.updateClient(client);
        LOGGER.info(E.LEAF.concat(E.LEAF) + mClient);
        return mClient;
    }
    public static final String DIRECTORY = "anchor";
    @PostMapping(value = "/uploadID", produces = MediaType.TEXT_PLAIN_VALUE)
    public String uploadID(@RequestParam("id") String id,
                           @RequestParam("idFront") MultipartFile idFront,
                           @RequestParam("idBack") MultipartFile idBack) throws Exception {

        LOGGER.info(E.RAIN_DROP.concat(E.RAIN_DROP) + "ClientController:uploadID... : ".concat(id));
        // Front of ID Card
        File dir = getDirectory();
        byte[] idFrontBytes = idFront.getBytes();
        File idFrontFile = new File(dir, "file_" + System.currentTimeMillis());
        Path idFrontPath = Paths.get(idFrontFile.getAbsolutePath());
        Files.write(idFrontPath, idFrontBytes);
        LOGGER.info("....... idFront file received: \uD83C\uDFBD "
                .concat(" length: " + idFrontFile.length() + " idFrontBytes"));
        fileService.uploadIDFront(id, idFrontFile);
        // Back of ID Card
        byte[] idBackBytes = idBack.getBytes();
        File idBackFile = new File(dir, "file_" + System.currentTimeMillis());
        Path idBackPath = Paths.get(idBackFile.getAbsolutePath());
        Files.write(idBackPath, idBackBytes);
        LOGGER.info("....... idBack file received: \uD83C\uDFBD "
                .concat(" length: " + idBackFile.length() + " idFrontBytes"));
        fileService.uploadIDBack(id, idBackFile);
        String msg = E.HAND2.concat("ID Documents have been uploaded");
        LOGGER.info("\uD83C\uDFBD \uD83C\uDFBD \uD83C\uDFBD Returned from upload .... OK!");
        return msg;
    }

    @PostMapping(value = "/uploadProofOfResidence", produces = MediaType.TEXT_PLAIN_VALUE)
    public String uploadProofOfResidence(@RequestParam("id") String id,
                           @RequestParam("proofOfResidence") MultipartFile proofOfResidence) throws Exception {

        LOGGER.info(E.RAIN_DROP.concat(E.RAIN_DROP) + "ClientController:uploadProofOfResidence...");
        // Front of ID Card
        File dir = getDirectory();
        byte[] proofOfResidenceBytes = proofOfResidence.getBytes();
        File proofFile = new File(dir, "file_" + System.currentTimeMillis());
        Path proofPath = Paths.get(proofFile.getAbsolutePath());
        Files.write(proofPath, proofOfResidenceBytes);
        LOGGER.info("....... proofOfResidence file received: \uD83C\uDFBD "
                .concat(" length: " + proofFile.length() + " proofOfResidenceBytes"));

        fileService.uploadProofOfResidence(id, proofFile);
        LOGGER.info("\uD83C\uDFBD \uD83C\uDFBD \uD83C\uDFBD Returned from upload .... OK!");
        return E.HAND2.concat("Proof of Residence document has been uploaded");
    }
    @PostMapping(value = "/uploadSelfie", produces = MediaType.TEXT_PLAIN_VALUE)
    public String uploadSelfie(@RequestParam("id") String id,
                                         @RequestParam("selfie") MultipartFile selfie) throws Exception {

        LOGGER.info(E.RAIN_DROP.concat(E.RAIN_DROP) + "ClientController:uploadSelfie ...");
        // Front of ID Card
        File dir = getDirectory();
        byte[] selfieBytes = selfie.getBytes();
        File selfieFile = new File(dir, "file_" + System.currentTimeMillis());
        Path selfiePath = Paths.get(selfieFile.getAbsolutePath());
        Files.write(selfiePath, selfieBytes);
        LOGGER.info("....... selfie file received: \uD83C\uDFBD "
                .concat(" length: " + selfieFile.length() + " selfieBytes"));

        fileService.uploadSelfie(id, selfieFile);
        LOGGER.info("\uD83C\uDFBD \uD83C\uDFBD \uD83C\uDFBD Returned from upload .... OK!");
        return E.HAND2.concat(E.ALIEN).concat("Selfie document has been uploaded");
    }

    private File getDirectory() {
        File dir = new File(DIRECTORY);
        if (!dir.exists()) {
            boolean done = dir.mkdir();
            LOGGER.info("Directory ".concat(DIRECTORY).concat(" has been created; done = " + done));
        }
        LOGGER.info("\uD83C\uDF3C Directory for temporary files: ".concat(dir.getAbsolutePath()));
        return dir;
    }

    @GetMapping(value = "/downloadSelfie", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] downloadSelfie(@RequestParam("id") String id) throws Exception {

        LOGGER.info(E.RAIN_DROP.concat(E.RAIN_DROP) + "ClientController:downloadSelfie...");
        // Front of ID Card
        File file = fileService.downloadSelfie(id);
        LOGGER.info("....... selfie file received: \uD83C\uDFBD "
                .concat(" length: " + file.length() + " file"));
        byte[] bytes = Files.readAllBytes(file.toPath());
        Files.delete(file.toPath());
        LOGGER.info(E.PEPPER.concat(E.PEPPER) + "downloadSelfie file deleted after creating output byte[]");
        return bytes;
    }
    @GetMapping(value = "/downloadProofOfResidence", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] downloadProofOfResidence(@RequestParam("id") String id) throws Exception {

        LOGGER.info(E.RAIN_DROP.concat(E.RAIN_DROP) + "ClientController:downloadProofOfResidence...");
        // Front of ID Card
        File file = fileService.downloadProofOfResidence(id);
        LOGGER.info("....... proofOfResidence file received: \uD83C\uDFBD "
                .concat(" length: " + file.length() + " file"));
        byte[] bytes = Files.readAllBytes(file.toPath());
        Files.delete(file.toPath());
        LOGGER.info(E.PEPPER.concat(E.PEPPER) + "downloadProofOfResidence file deleted after creating output byte[]");
        return bytes;
    }
    /**
     * Download the image of the front of an identity card
     * @param id clientId or agentId
     * @return image bytes
     * @throws Exception file not found
     */
    @GetMapping(value = "/downloadIDFront", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] downloadIDFront(@RequestParam("id") String id) throws Exception {

        LOGGER.info(E.RAIN_DROP.concat(E.RAIN_DROP) + "ClientController:downloadIDFront...");
        // Front of ID Card
        File file = fileService.downloadIDFront(id);
        LOGGER.info("....... idFront file downloaded: \uD83C\uDFBD "
                .concat(" length: " + file.length() + " file"));

        byte[] bytes = Files.readAllBytes(file.toPath());
        Files.delete(file.toPath());
        LOGGER.info(E.PEPPER.concat(E.PEPPER) + "downloadIDFront file deleted after creating output byte[]");
        return bytes;
    }
    /**
     * Download the image of the back of an identity card
     * @param id clientId or agentId
     * @return image bytes
     * @throws Exception file not found
     */
    @GetMapping(value = "/downloadIDBack", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] downloadIDBack(@RequestParam("id") String id) throws Exception {

        LOGGER.info(E.RAIN_DROP.concat(E.RAIN_DROP) + "ClientController:downloadIDBack...");
        // Back of ID Card
        File file = fileService.downloadIDBack(id);
        LOGGER.info("....... idFront file downloaded: \uD83C\uDFBD "
                .concat(" length: " + file.length() + " file"));

        byte[] bytes = Files.readAllBytes(file.toPath());
        Files.delete(file.toPath());
        LOGGER.info(E.PEPPER.concat(E.PEPPER) + "downloadIDBack file deleted after creating output byte[]");
        return bytes;
    }

    private static final String em ="\uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E ";
}
