package com.jannchie.biliob;

import com.jannchie.biliob.model.Video;
import com.jannchie.biliob.service.VideoService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

public class VideoTests extends BiliobApplicationTests {
    @Autowired
    private VideoService videoService;

    @Test
    public void testgetVideo() {
        Page v = videoService.getVideo(-1L, null, 0, 20);
        Assert.assertEquals("未处理text为空", "sd", v.toString());
        Assert.assertNotNull("无法成功获取Video", videoService.getVideo(-1L, "", 0, 20));
        Assert.assertNotNull("无法成功通过文本搜索Video", videoService.getVideo(-1L, "Jannchie", 0, 20));
    }

}
