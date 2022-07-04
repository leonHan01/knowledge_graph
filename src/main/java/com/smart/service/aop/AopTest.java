package com.smart.service.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Aspect
public class AopTest {

//
//    //这里需要注意了，这个是将自己自定义注解作为切点的根据，路径一定要写正确了
//    @Pointcut(value = "@annotation(com.smart.service.aop.LogTrack)")
//    public void access() {
//
//    }


    //环绕增强，是在before前就会触发
    @Around("@annotation(logTrack)")
    public Object around(ProceedingJoinPoint pjp, LogTrack logTrack) throws Throwable {
        System.out.println("-aop 日志环绕阶段-" + new Date());
        return pjp.proceed();

    }

}