package com.jannchie.biliob.controller;

import com.jannchie.biliob.constant.ResultEnum;
import com.jannchie.biliob.model.Bangumi;
import com.jannchie.biliob.service.DamnYouService;
import com.jannchie.biliob.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * @author Jannchie
 */
@Controller
public class DamnYouController {
    @Autowired
    public DamnYouService damnYouService;

    @RequestMapping(method = RequestMethod.POST, value = "/api/damn-you/upload")
    public ResponseEntity<Result<String>> uploadData(
            @RequestParam("file") MultipartFile file) throws IOException {
        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 获取文件后缀
        assert fileName != null;
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        final File tempFile = File.createTempFile(fileName, prefix);
        file.transferTo(tempFile);
        ZipFile zipFile = new ZipFile(tempFile);
        InputStream inputStream = file.getInputStream();
        ZipInputStream zipInputStream = new ZipInputStream(inputStream, Charset.defaultCharset());
        damnYouService.deleteFile(tempFile);
        damnYouService.saveData(zipInputStream, zipFile);
        return ResponseEntity.accepted().body(new Result<>(ResultEnum.ACCEPTED));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/damn-you/upload/history")
    public ResponseEntity<Result<String>> uploadHistory(
            @RequestParam("file") MultipartFile file) throws IOException {
        damnYouService.saveHistoryData(file);
        return ResponseEntity.accepted().body(new Result<>(ResultEnum.ACCEPTED));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/damn-you/upload/info")
    public ResponseEntity<Result<String>> uploadInfo(
            @RequestParam("file") MultipartFile file) throws IOException {
        damnYouService.saveInfoData(file);
        return ResponseEntity.accepted().body(new Result<>(ResultEnum.ACCEPTED));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/damn-you/bangumi/detail")
    public ResponseEntity<?> getDetail(@RequestParam("sid") Long sid) {
        return ResponseEntity.ok(damnYouService.getDetail(sid));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/damn-you/bangumi/info")
    public ResponseEntity<List<Bangumi>> listInfo(
            @RequestParam("p") Integer page,
            @RequestParam("ps") Long pageSize,
            @RequestParam(value = "kw", defaultValue = "") String keyword) {
        return ResponseEntity.ok(damnYouService.listInfo(page, pageSize, keyword));
    }
}
