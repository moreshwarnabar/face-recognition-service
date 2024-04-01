package com.app.webtier.service;

import org.springframework.web.multipart.MultipartFile;

public interface IS3Service {

    boolean saveImage(String key, MultipartFile file);

}
