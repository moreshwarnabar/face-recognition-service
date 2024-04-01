package com.app.webtier.service;

import org.springframework.web.multipart.MultipartFile;

public interface IWebTierService {

    public String processImages(MultipartFile files);

}
