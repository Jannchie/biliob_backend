package com.jannchie.biliob.utils;

import com.jannchie.biliob.model.Author;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author Jannchie
 */
public class Interruption {
    /**
     * author data interruption
     *
     * @param dataList data list
     * @param step     time step
     * @return interrupted
     */
    public static List<Author.Data> authorDataInterruption(List<Author.Data> dataList, Integer step) {
        int size = dataList.size();
        if (size <= 1) {
            return dataList;
        }
        List<Author.Data> result = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(dataList.get(0).getDatetime().getTime() - (dataList.get(0).getDatetime().getTime() % 86400000));
        int i = 1;
        while (i < size) {
            Author.Data cData = dataList.get(i);
            Author.Data pData = dataList.get(i - 1);
            if (cData.getDatetime().getTime() == c.getTime().getTime()) {
                result.add(cData);
            }

            if (cData.getDatetime().after(c.getTime()) && pData.getDatetime().before(c.getTime())) {
                Author.Data newData = new Author.Data();
                long deltaTime = c.getTimeInMillis() - pData.getDatetime().getTime();
                for (int j = 0; j < 3; j++) {
                    Long cValue;
                    Long pValue;
                    Long value;
                    if (j == 0) {
                        cValue = cData.getFans();
                        pValue = pData.getFans();
                    } else if (j == 1) {
                        cValue = cData.getArchiveView();
                        pValue = pData.getArchiveView();
                    } else {
                        cValue = cData.getLike();
                        pValue = pData.getLike();
                    }
                    value = pValue + (cValue - pValue) * deltaTime / (cData.getDatetime().getTime() - pData.getDatetime().getTime());
                    if (j == 0) {
                        newData.setFans(value);
                    } else if (j == 1) {
                        newData.setArchiveView(value);
                    } else {
                        newData.setLike(value);
                    }
                }
                newData.setDatetime(c.getTime());
                result.add(newData);
            }
            c.add(Calendar.MILLISECOND, step);
            if (c.getTimeInMillis() > cData.getDatetime().getTime()) {
                i++;
            }
        }
        return result;
    }

}
