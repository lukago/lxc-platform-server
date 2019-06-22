package org.platform.lxc.exception;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandlerController {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandlerController.class);

  @Bean
  public ErrorAttributes errorAttributes() {
    // Hide exception field in the return object
    return new DefaultErrorAttributes() {
      @Override
      public Map<String, Object> getErrorAttributes(WebRequest request, boolean includeStackTrace) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, includeStackTrace);
        errorAttributes.remove("exception");
        errorAttributes.remove("trace");
        errorAttributes.remove("error");
        errorAttributes.remove("errors");
        return errorAttributes;
      }
    };
  }

  @ExceptionHandler(HttpException.class)
  public void handleCustomException(HttpServletResponse res, HttpException ex) throws IOException {
    log.info("Sending error HttpException caucht: {}", ex);
    res.sendError(ex.getHttpStatus().value(), ex.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public void handleAccessDeniedException(HttpServletResponse res) throws IOException {
    log.info("Sending error AccessDeniedException caucht");
    res.sendError(HttpStatus.FORBIDDEN.value(), "Access denied");
  }

  @ExceptionHandler(Exception.class)
  public void handleException(HttpServletResponse res) throws IOException {
    log.info("Sending error Exception caucht");
    res.sendError(HttpStatus.BAD_REQUEST.value(), "Something went wrong");
  }

}
