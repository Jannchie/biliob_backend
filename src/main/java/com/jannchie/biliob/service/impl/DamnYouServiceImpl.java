package com.jannchie.biliob.service.impl;

import com.jannchie.biliob.model.Bangumi;
import com.jannchie.biliob.model.BangumiData;
import com.jannchie.biliob.service.DamnYouService;
import com.mongodb.MongoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@EnableAsync
@Service
public class DamnYouServiceImpl implements DamnYouService {
    private static final Logger logger = LogManager.getLogger();
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
                String[] params = line.split("\t", -1);
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
                logger.error(line);
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
    public void saveInfoData(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        saveInfoDataFromText(bufferedReader);
        bufferedReader.close();
    }

    private void saveInfoDataFromText(BufferedReader bufferedReader) throws IOException {
        String line;
        Calendar pubCalendar = Calendar.getInstance();
        Calendar updateCalendar = Calendar.getInstance();
        while ((line = bufferedReader.readLine()) != null) {
            String[] params = line.split("\t", -1);
            try {
                pubCalendar.setTimeInMillis(Long.parseLong(params[7]));
                updateCalendar.setTimeInMillis(Long.parseLong(params[20]));
                Long sid = Long.valueOf(params[0]);
                Bangumi bangumi = new Bangumi(
                        sid,
                        Long.valueOf(params[1]),
                        String.valueOf(params[2]),
                        String.valueOf(params[3]),
                        String.valueOf(params[4]),
                        Short.valueOf(params[5]),
                        Byte.valueOf(params[6]),
                        pubCalendar.getTime(),
                        Boolean.valueOf(params[8]),
                        Boolean.valueOf(params[9]),
                        Long.valueOf(params[10]),
                        Long.valueOf(params[11]),
                        Long.valueOf(params[12]),
                        Long.valueOf(params[13]),
                        Float.valueOf(params[14]),
                        Long.valueOf(params[15]),
                        String.valueOf(params[16]),
                        String.valueOf(params[17]),
                        String.valueOf(params[18]),
                        String.valueOf(params[19]),
                        updateCalendar.getTime()
                );
                mongoTemplate.save(bangumi);
            } catch (Exception e) {
                logger.error(line);
                logger.error(Arrays.toString(params));
                e.printStackTrace();
            }
        }
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
