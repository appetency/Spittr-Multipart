package spittr.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice   /*定义控制器类*/
public class AppWideExceptionHandler {

  @ExceptionHandler(DuplicateSpittleException.class)  /*定义异常方法*/
  public String handleNotFound() {
    return "error/duplicate";
  }

}
