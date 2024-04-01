package com.app.apptier.service;

import java.nio.file.Path;

public interface IS3Service {

    Path downloadImage(String imageName);

    void saveResult(String imageName, String result);

}
