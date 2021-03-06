package com.nowcoder.aspect;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Before("execution(* com.nowcoder.controller.indexController.*(..))")
    public void beforeMethod(JoinPoint joinPoint){
        StringBuilder sb = new StringBuilder();
        for(Object arg: joinPoint.getArgs()){
            sb.append("args" + arg.toString() + "|");
        }
        logger.info("before method " + sb.toString());
    }

    @After("execution(* com.nowcoder.controller.indexController.*(..))")
    public void afterMethod(JoinPoint joinPoint){
        StringBuilder sb = new StringBuilder();
        for(Object arg: joinPoint.getArgs()){
            sb.append("args" + arg.toString() + "|");
        }
        logger.info("after method " + sb.toString());
    }
}
