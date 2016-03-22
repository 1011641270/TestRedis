/**
 * This File is created by hztianduoduo at 2016骞�鏈�5鏃�any questions please have a message on me!
 */
package com.tian.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author hztiandd@tian-dd.top
 * 
 * 2016骞�鏈�5鏃�
 */
public class TestRedis {
    
    public static void main(String[] args) throws Exception{
        
        @SuppressWarnings("resource")
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "applicationContext-redis.xml");
        
        NotaryCacheService notaryCacheService = (NotaryCacheService) context.getBean("notaryCacheService");
        
        notaryCacheService.putNotaryCacheAttribute("tdd+20122330","tdd","1234567890");

        String uid = "1111@yeah.net";
        String mid = "tdd";
        
        /*Map<String, String> datas = new HashMap<>();
        
        datas.put("mid", "1234567890");

        notaryCacheService.setNotaryCache(uid+mid, datas);
        
        Map<String, String> map2 = notaryCacheService.getNotaryCache(uid+mid);
        
        System.out.println(map2.size());*/
        
        //1.楠岃瘉瓒呮椂闂
        
        notaryCacheService.saveDataInRangeTime(mid, uid);
        
        System.out.println(notaryCacheService.getData(mid));
        
        System.out.println(notaryCacheService.saveDataInRangeTime(mid, "1"));
        
        System.out.println(notaryCacheService.getData(mid));
        
    }

}
