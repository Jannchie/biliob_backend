package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.model.BangumiData;
import com.jannchie.biliob.service.DamnYouService;
import com.mongodb.MongoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@EnableAsync
@Service
public class DamnYouServiceImpl implements DamnYouService {
    @Autowired
    MongoTemplate mongoTemplate;

    @Async
    @Override
    public void saveData(ZipInputStream zipInputStream, ZipFile zipFile) throws MongoException, IOException {
        ZipEntry zipEntry;
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            if (zipEntry.toString().endsWith("txt")) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(zipFile.getInputStream(zipEntry)));
                saveHistoryDataFromTxt(br);
                br.close();

            }
        }
    }

    public void saveHistoryDataFromTxt(BufferedReader br) throws IOException {
        String line;
        Calendar c = Calendar.getInstance();
        while ((line = br.readLine()) != null) {
            try {
                String[] params = line.split("\t");
                c.setTimeInMillis(Long.parseLong(params[10]));
                Long sid = Long.valueOf(params[0]);
                BangumiData bangumiData = new BangumiData(
                        sid,
                        Long.valueOf(params[1]),
                        Long.valueOf(params[2]),
                        Long.valueOf(params[3]),
                        Integer.valueOf(params[4]),
                        Integer.valueOf(params[5]),
                        Integer.valueOf(params[6]),
                        Integer.valueOf(params[7]),
                        Float.valueOf(params[8]),
                        Integer.valueOf(params[9]),
                        c.getTime()
                );
                if (!mongoTemplate.exists(Query.query(Criteria.where("sid").is(sid).and("datetime").is(c.getTime())), BangumiData.class)) {
                    mongoTemplate.save(bangumiData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Async
    @Override
    public void saveHistoryData(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        saveHistoryDataFromTxt(bufferedReader);
        bufferedReader.close();
    }

    @Async
    @Override
    public void deleteFile(File... files) {
        for (File file : files) {
            if (file.exists()) {
                boolean status = file.delete();
                return;
            }
        }
    }
}
