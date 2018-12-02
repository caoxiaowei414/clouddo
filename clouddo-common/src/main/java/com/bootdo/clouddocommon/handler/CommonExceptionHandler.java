package com.bootdo.clouddocommon.handler;

import com.bootdo.clouddocommon.enums.GlobalEnum;
import com.bootdo.clouddocommon.utils.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CommonExceptionHandler {


    /*入参校验异常*/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleBindException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        log.info("必填校验异常: {}({})", fieldError.getDefaultMessage(),fieldError.getField());
        return ResultVO.error(GlobalEnum.PARAMETER_EXCEPTION.getCode() , fieldError.getDefaultMessage());
    }


    /*入参校验异常*/
    @ExceptionHandler({BindException.class})
    public Object handleBindException(BindException ex ) {
        //校验 除了 requestbody 注解方式的参数校验 对应的 bindingresult 为 BeanPropertyBindingResult
        FieldError fieldError = ex.getBindingResult().getFieldError();
        log.info("必填校验异常:{}({})", fieldError.getDefaultMessage(),fieldError.getField());
        return ResultVO.error(GlobalEnum.PARAMETER_EXCEPTION.getCode() , fieldError.getDefaultMessage());
    }


    @ExceptionHandler(RuntimeException.class)
    ResultVO runtimeException(RuntimeException e) {

        return ResultVO.error(GlobalEnum.RUNTIMEEXCEPTION.getCode(), GlobalEnum.RUNTIMEEXCEPTION.getMessage());
    }

    @ExceptionHandler(Exception.class)
    ResultVO exception(Exception e){
        return ResultVO.error(GlobalEnum.ERROE500.getCode(), e.getMessage());
    }
}
