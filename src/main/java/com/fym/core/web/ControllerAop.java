package com.fym.core.web;

import com.fym.core.err.OpException;
import com.fym.core.err.OpResult;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * Created by fengy on 2016/5/18.
 */
@Aspect
@Component
public class ControllerAop {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerAop.class);

//    @Autowired
//    private PermissionCom permissionCom;
//
//
//    @Autowired
//    private WebContextHolder webContextHolder;
//
//
//    @Autowired
//    private UserService userService;

    //配置切入点,该方法无方法体,主要为方便同类中其他方法使用此处配置的切入点
    @Pointcut("execution(@org.springframework.web.bind.annotation.ResponseBody * com.fym..*Controller.*(..))")
    public void aspect() {
    }


    //配置环绕通知,使用在方法aspect()上注册的切入点
    @Around("aspect()")
    public Object around(JoinPoint joinPoint) {
//        //先获取path作为权限permItem
//        String path = "";
//        try {
//            //拦截的实体类
//            Object target = joinPoint.getTarget();
//            //拦截的方法名称
//            String methodName = joinPoint.getSignature().getName();
//            //拦截的方法参数
//            Object[] args = joinPoint.getArgs();
//            //拦截的放参数类型
//            Class[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();
//            Method m = null;
//            //通过反射获得拦截的method
//            m = target.getClass().getMethod(methodName, parameterTypes);
//            //TODO future 改成缓存
//            RequestMapping classAnno = target.getClass().getAnnotation(RequestMapping.class);
//            if (classAnno != null && classAnno.value().length > 0) {
//                path = classAnno.value()[0];
//            }
//            RequestMapping methodAnno = m.getAnnotation(RequestMapping.class);
//            if (methodAnno != null && methodAnno.value().length > 0) {
//                path = path + methodAnno.value()[0];
//            }
//        } catch (SecurityException e) {
//            LOGGER.error("发生系统错误:" + e.getMessage());
//            e.printStackTrace();
//            return new OpResult(OpResult.SYSERROR, OpResult.STR_SYSERROR + e.getMessage());
//        } catch (NoSuchMethodException e) {
//            LOGGER.error("发生系统错误:" + e.getMessage());
//            e.printStackTrace();
//            return new OpResult(OpResult.SYSERROR, OpResult.STR_SYSERROR + e.getMessage());
//        }
//
//        PlayerLoginS loginS = this.userService.getLogin();
//        if (loginS == null) {
//            //需要权限访问，所以根据cookie的ticket获取登录ticket
//            Cookie[] cookies = webContextHolder.getRequest().getCookies();
//            String t = null;
//            if (cookies != null) {
//                for (Cookie cookie : cookies) {
//                    if (cookie.getName().equals("t")) {
//                        t = cookie.getValue();
//                        try {
//                            loginS = this.userService.loginTicket(t);
//                        } catch (OpException e) {
////                            return e.opResult;
//                        }
//                        continue;
//                    }
//                }
//            }
//        }
//
//        if (this.permissionCom.permissionItemExist(path)) {
//            if (loginS == null) {
//                return new OpResult(OpResult.FAIL, "只有已注册用户可以访问，请登录或注册");
//            }
//            if (!loginS.permItems.contains(path)) {
//                return new OpResult(OpResult.FAIL, this.permissionCom.getErrMsg(path));
//            }
//            //TODO future 整体功能可以前需要改回来
////            if (loginS.username.equals("admin")) {
////                if (!this.permissionCom.isAdminPermItem(path)) {
////                    return new OpResult(OpResult.FAIL, "这不是超级管理员的权限");
////                }
////            } else if (!this.roleService.checkPermission(loginS.roleids, path)) {
////                return new OpResult(OpResult.FAIL, "用户没有权限");
////            }
//        }

        //记录request的执行时间
        Object ret = null;
        long start = System.currentTimeMillis();
        try {
            //执行Controller
            ret = ((ProceedingJoinPoint) joinPoint).proceed();
            long end = System.currentTimeMillis();
            if (LOGGER.isInfoEnabled()) {
//                LOGGER.info(loginS == null ? "游客" : loginS.displayName + "访问了页面" + path);
                LOGGER.info("around " + joinPoint + "\tUse time : " + (end - start) + " ms!");
            }
        } catch (OpException e) {
            long end = System.currentTimeMillis();
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("around " + joinPoint + "\tUse time : " + (end - start) + " ms with exception : " + e.getMessage());
            }
            ret = new OpResult(e.opResult.opCode,
                    (e.opResult.opCode == OpResult.INVALID ? "程序发生错误,请联系技术人员. " : "") + e.opResult.message);
        } catch (Throwable e) {
            long end = System.currentTimeMillis();
            LOGGER.error("发生系统错误:" + e.getMessage());
            e.printStackTrace();
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("around " + joinPoint + "\tUse time : " + (end - start) + " ms with sys exception : " + e.getMessage());
            }
            ret = new OpResult(OpResult.SYSERROR, OpResult.STR_SYSERROR + e.getMessage());
        }
        return ret;
    }


    /*
     * 配置前置通知,使用在方法aspect()上注册的切入点
     * 同时接受JoinPoint切入点对象,可以没有该参数
     */
//    @Before("deleteSupplierAspect()")
//    public void before(JoinPoint joinPoint) throws OpException {
//    }
//
//    //配置后置通知,使用在方法aspect()上注册的切入点
//    @After("deleteSupplierAspect()")
//    public void after(JoinPoint joinPoint){
//        if(LOGGER.isInfoEnabled()){
//            LOGGER.info("after " + joinPoint);
//        }
//    }

//    //配置后置返回通知,使用在方法aspect()上注册的切入点
//    @AfterReturning("deleteSupplierAspect()")
//    public void afterReturn(JoinPoint joinPoint){
//        if(LOGGER.isInfoEnabled()){
//            LOGGER.info("afterReturn " + joinPoint);
//        }
//    }
//
//    //配置抛出异常后通知,使用在方法aspect()上注册的切入点
//    @AfterThrowing(pointcut="deleteSupplierAspect()", throwing="ex")
//    public void afterThrow(JoinPoint joinPoint, Exception ex){
//        if(LOGGER.isInfoEnabled()){
//            LOGGER.info("afterThrow " + joinPoint + "\t" + ex.getMessage());
//        }
//    }

}
