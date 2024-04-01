package com.app.webtier.controller;

import com.app.webtier.service.IWebTierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Methods to expose Public APIs for receiving images that need to be classified
 *
 * @author Moreshwar Nabar
 */
@RestController
@RequestMapping("/")
public class WebTierController {

    private static final Logger LOG = LoggerFactory.getLogger(WebTierController.class);

    private final IWebTierService webTierService;

    public WebTierController(IWebTierService webTierService) {
        this.webTierService = webTierService;
    }

    /**
     * Receives the image file that needs to be classified and returns the result of the classification
     *
     * @param file The image to be classified
     * @return The classification result
     */
    @PostMapping
    public String postFileUpload(@RequestParam("inputFile") MultipartFile file) {
        LOG.info("Request received to classify an image!");
        String result = webTierService.processImages(file);
        LOG.info("Sending response: {}", result);
        return result;
    }

}
