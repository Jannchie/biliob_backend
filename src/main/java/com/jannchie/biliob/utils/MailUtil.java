package com.jannchie.biliob.utils;

import com.jannchie.biliob.constant.ResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Map;

/**
 * @author jannchie
 */
@Component
public class MailUtil {
    private static final String ACTIVATION_CODE_COLLECTION = "activation_code";
    private final JavaMailSender mailSender;
    private final MongoTemplate mongoTemplate;

    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    public MailUtil(JavaMailSender mailSender, MongoTemplate mongoTemplate) {
        this.mailSender = mailSender;
        this.mongoTemplate = mongoTemplate;
    }

    public Boolean checkActivationCode(String mail, String activationCode) {

        Boolean checkStatus =
                mongoTemplate.exists(
                        Query.query(Criteria.where("mail").is(mail).and("code").is(activationCode)),
                        MailUtil.ACTIVATION_CODE_COLLECTION);
        if (checkStatus) {
            mongoTemplate.findAndRemove(
                    Query.query(Criteria.where("mail").is(mail)), Map.class,
                    MailUtil.ACTIVATION_CODE_COLLECTION);
        }
        return checkStatus;
    }

    public ResponseEntity<?> sendActivationCode(String receiver) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        if (mongoTemplate.exists(Query.query(Criteria.where("mail").is(receiver)), "user")) {
            return new ResponseEntity<>(
                    new Result<>(ResultEnum.MAIL_HAD_BEEN_REGISTERED), HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            helper.setTo(receiver);
            helper.setSubject("BiliOB观测者 - 邮箱验证");
            String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
            String temp =
                    "<div style=\"clear: both;\"><span style=\"font-family: simsun, STSongti-SC-Regular;\">BiliOB观测者接收到了您的请求。</span><br></div><div style=\"clear: both;\"><div style=\"margin: 0px; padding: 0px; border: 0px; outline: 0px; color: rgb(0, 0, 0); font-family: Tahoma, Arial, STHeiti, SimSun; font-size: 14px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; text-align: start; text-indent: 0px; text-transform: none; text-decoration-style: initial; text-decoration-color: initial; clear: both;\"><div class=\" __aliyun_node_has_bgcolor\" style=\"margin: 0px; padding: 0px; border: 0px; outline: 0px; font-variant-ligatures: normal; font-variant-caps: normal; text-align: start; text-indent: 0px; background-color: rgb(255, 255, 255); text-decoration-style: initial; text-decoration-color: initial; clear: both;\"><span class=\" __aliyun_node_has_color\" style=\"margin: 0px; padding: 0px; border: 0px; outline: 0px; color: rgb(0, 0, 0); font-size: 14px; font-style: normal; font-weight: 400; text-transform: none; font-family: simsun, STSongti-SC-Regular;\">请妥善使用这段代码：</span></div><div class=\" __aliyun_node_has_bgcolor\" style=\"margin: 0px; padding: 0px; border: 0px; outline: 0px; font-variant-ligatures: normal; font-variant-caps: normal; text-align: start; text-indent: 0px; background-color: rgb(255, 255, 255); text-decoration-style: initial; text-decoration-color: initial; clear: both;\"><span class=\" __aliyun_node_has_color\" style=\"margin: 0px; padding: 0px; border: 0px; outline: 0px; font-size: 14px; font-style: normal; text-transform: none; color: rgb(84, 141, 212); font-weight: 700; font-family: &quot;arial black&quot;;\">%s</span></div><div class=\" __aliyun_node_has_bgcolor\" style=\"margin: 0px; padding: 0px; border: 0px; outline: 0px; font-variant-ligatures: normal; font-variant-caps: normal; text-align: start; text-indent: 0px; background-color: rgb(255, 255, 255); text-decoration-style: initial; text-decoration-color: initial; clear: both;\"><span class=\" __aliyun_node_has_color\" style=\"margin: 0px; padding: 0px; border: 0px; outline: 0px; color: rgb(0, 0, 0); font-size: 14px; font-style: normal; font-weight: 400; text-transform: none; font-family: simsun, STSongti-SC-Regular;\">——观测站的管理员：Jannchie见齐</span></div></div></div>";
            String text = String.format(temp, code);
            helper.setText(text, true);
            mongoTemplate.upsert(
                    Query.query(Criteria.where("mail").is(receiver)),
                    Update.update("mail", receiver).set("code", code).set("createTime", new Date()),
                    MailUtil.ACTIVATION_CODE_COLLECTION);
            mailSender.send(message);
        } catch (Exception e) {
            return new ResponseEntity<>(new Result<>(ResultEnum.SEND_MAIL_FAILED), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Result<>(ResultEnum.SUCCEED), HttpStatus.OK);
    }
}
