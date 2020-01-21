package com.jannchie.biliob.exception.handler;

import com.jannchie.biliob.exception.BusinessException;
import com.jannchie.biliob.utils.ExceptionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author jannchie
 */
@ControllerAdvice
public class BusinessExceptionHandler {
    private static final Logger logger = LogManager.getLogger(BusinessExceptionHandler.class);

    /**
     * Handle the business exception.
     *
     * @param businessException The business exception.
     * @return The exception code and message.
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ExceptionResult businessExceptionHandler(BusinessException businessException) {

        // Log the exception information.
        logger.warn(businessException.getExceptionEnum());
        return new ExceptionResult(businessException.getExceptionEnum());
    }
}
