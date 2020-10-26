package com.jannchie.biliob.utils;

import com.jannchie.biliob.constant.VideoChannel;
import com.jannchie.biliob.model.Video;
import com.jannchie.biliob.model.VideoInfo;

import java.util.Calendar;

/**
 * @author Jannchie
 */
public class VideoAdapter {
    static public Video getVideoFromInfo(VideoInfo videoInfo) {
        Video v = new Video();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(videoInfo.getCtime());
        v.setAid(videoInfo.getAid());
        v.setAuthor(videoInfo.getOwner());
        v.setTag(videoInfo.getTag());
        v.setAuthorName(videoInfo.getOwner().getName());
        v.setcCoin(Math.toIntExact(videoInfo.getStat().getCoin()));
        v.setcDanmaku(Math.toIntExact(videoInfo.getStat().getDanmaku()));
        v.setcDatetime(videoInfo.getStat().getDatetime());
        v.setcFavorite(Math.toIntExact(videoInfo.getStat().getFavorite()));
        v.setcLike(Math.toIntExact(videoInfo.getStat().getLike()));
        v.setcJannchie(videoInfo.getStat().getJannchie());
        v.setcView(videoInfo.getStat().getView());
        v.setcShare(Math.toIntExact(videoInfo.getStat().getShare()));
        v.setChannel(VideoChannel.getName(videoInfo.getTid()));
        v.setcReply(Math.toIntExact(videoInfo.getStat().getReply()));
        v.setDatetime(c.getTime());
        v.setFocus(true);
        v.setSubChannel(videoInfo.getTname());
        v.setPic(videoInfo.getPic());
        v.setBvid(videoInfo.getBvid());
        v.setMid(v.getAuthor().getMid());
        v.setTitle(v.getTitle());
        return v;
    }
}
