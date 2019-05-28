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

import javax.mail.MessagingException;
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

  public ResponseEntity sendActivationCode(String receiver) {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = null;
    if (mongoTemplate.exists(Query.query(Criteria.where("mail").is(receiver)), "user")) {
      return new ResponseEntity<>(
          new Result(ResultEnum.MAIL_HAD_BEEN_REGISTERED), HttpStatus.NOT_ACCEPTABLE);
    }

    try {
      helper = new MimeMessageHelper(message, true);
      helper.setFrom(sender);
      helper.setTo(receiver);
      helper.setSubject("邮件测试");
      String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
      String temp =
          "<div style=\"margin: auto;text-align: center; color: dimgray; max-width: 800px\">\n"
              + "    <div>\n"
              + "        <img src=\"https://www.biliob.com/img/icons/android-chrome-192x192.png\">\n"
              + "    </div>\n"
              + "    观测者您好！您刚刚通过邮箱在BiliOB观测站申请了验证码\n"
              + "    <h2 style=\"color: dimgray\">您获得的验证码为</h2>\n"
              + "    <h1 style=\"color:dodgerblue\"> %s </h1>\n"
              + "    若非本人操作，你懂得QwQ，请忽略本邮件~\n"
              + "    <p style=\"text-align: end\">From Jannchie见齐</p>\n"
              + "</div>";
      String text = String.format(temp, code);
      helper.setText(text, true);
      mongoTemplate.upsert(
          Query.query(Criteria.where("mail").is(receiver)),
          Update.update("mail", receiver).set("code", code).set("createTime", new Date()),
          MailUtil.ACTIVATION_CODE_COLLECTION);
      mailSender.send(message);
    } catch (MessagingException e) {
      e.printStackTrace();
      return new ResponseEntity<>(new Result(ResultEnum.SEND_MAIL_FAILED), HttpStatus.OK);
    }
    return new ResponseEntity<>(new Result(ResultEnum.SUCCEED), HttpStatus.OK);
  }
}
