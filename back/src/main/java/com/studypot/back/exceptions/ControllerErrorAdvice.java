package com.studypot.back.exceptions;

import java.io.IOException;
import java.util.Enumeration;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class ControllerErrorAdvice extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    ServletWebRequest servletWebRequest = (ServletWebRequest) request;

    log.warn(
        "존재하지 않는 페이지 요청입니다.\n" +
            "url={} {}",
        servletWebRequest.getHttpMethod(), servletWebRequest.getRequest().getRequestURI()
    );

    return ResponseEntity.notFound().build();
  }

  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    ServletWebRequest servletWebRequest = (ServletWebRequest) request;

    log.warn(
        "허용되지 않은 요청 메소드입니다.\n" +
            "url={} {}",
        servletWebRequest.getHttpMethod(), servletWebRequest.getRequest().getRequestURI()
    );

    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleException(Exception exception, HttpServletRequest request) throws IOException {
    log.error(
        "🚨 알 수 없는 에러가 발생했습니다.\n" +
            "url={} {}{}\n" +
            "headers={}\n" +
            "body={}"
        ,
        request.getMethod(), request.getRequestURI(), request.getQueryString() != null ? "?" + request.getQueryString() : "",
        requestHeaderString(request),
        request.getReader().lines().collect(Collectors.joining("\n")),
        exception
    );

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
  }

  private String requestHeaderString(HttpServletRequest request) {
    StringBuilder headerString = new StringBuilder("{");

    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();

      headerString.append(headerName);
      headerString.append("=");
      headerString.append(request.getHeader(headerName));
      headerString.append(", ");
    }

    headerString.append("}");

    return headerString.toString();
  }
}
