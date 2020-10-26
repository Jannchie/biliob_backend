package com.jannchie.biliob.constant;

/**
 * @author Jannchie
 */
public class VideoChannel {
    public static String getName(Short tid) {
        switch (tid) {
            case 1:
                return "动画";
            case 13:
                return "番剧";
            case 167:
                return "国创";
            case 3:
                return "音乐";
            case 129:
                return "舞蹈";
            case 4:
                return "游戏";
            case 17:
                return "单机游戏";
            case 36:
                return "知识";
            case 188:
                return "数码";
            case 160:
                return "生活";
            case 138:
                return "搞笑";
            case 76:
                return "美食圈";
            case 75:
                return "动物圈";
            case 119:
                return "鬼畜";
            case 155:
                return "时尚";
            case 202:
                return "资讯";
            case 165:
                return "广告";
            case 5:
                return "娱乐";
            case 181:
                return "影视";
            case 177:
                return "纪录片";
            case 23:
                return "电影";
            case 11:
                return "电视剧";
            default:
                return "其他";
        }
    }
}
