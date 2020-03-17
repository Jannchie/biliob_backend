package com.jannchie.biliob.service;

import com.jannchie.biliob.model.Bangumi;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
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
     * delete file
     *
     * @param files file
     */
    void deleteFile(File... files);

    /**
     * save history data
     *
     * @param file data
     * @throws IOException io exception
     */
    void saveHistoryData(MultipartFile file) throws IOException;

    /**
     * save info data
     *
     * @param file data
     * @throws IOException io exception
     */
    void saveInfoData(MultipartFile file) throws IOException;

    /**
     * list bangumi info
     *
     * @param page     page
     * @param pageSize page size
     * @param keyword
     * @return bangumi info list
     */
    List<Bangumi> listInfo(Integer page, Long pageSize, String keyword);

    /**
     * get history by sid
     *
     * @param sid series id
     * @return bangumi history data
     */
    Bangumi getDetail(Long sid);
}
