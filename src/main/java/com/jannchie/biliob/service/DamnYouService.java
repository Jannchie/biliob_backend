package com.jannchie.biliob.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * @author Jannchie
 */
@Service
public interface DamnYouService {


    /**
     * @param zipInputStream zip stream
     * @param zipFile        zip file
     */
    void saveData(ZipInputStream zipInputStream, ZipFile zipFile) throws IOException;

    /**
     * @param files file
     */
    void deleteFile(File... files);

    void saveHistoryData(MultipartFile file) throws IOException;

    void saveInfoData(MultipartFile file) throws IOException;
}
