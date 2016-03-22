/**
 * This File is created by hztianduoduo at 2016骞�鏈�5鏃�any questions please have a message on me!
 */
package com.tian.redis;

import java.util.Map;

/**
 * @author hztiandd@tian-dd.top
 * 
 * 2016骞�鏈�5鏃�
 */
public class NotaryCacheServiceImpl implements NotaryCacheService{

    private static final String CACHE_KEY_RREFIX = "CacheId:";
    
    //瓒呮椂鏃堕棿
    private int cacheTimeout;

    private RedisClient redisClient;

    @Override
    public void removeNotaryCacheAttribute(String id, String key) {
        
        try {

            id = CACHE_KEY_RREFIX + id;
            redisClient.delMapItem(id, key);
            
        } catch (Exception e) {
        }
        
    }

    @Override
    public void putNotaryCacheAttribute(String id, String key, String value) {
        
        try {
            
            id = CACHE_KEY_RREFIX + id;
            redisClient.setMapItem(id, key, value);
            
        } catch (Exception e) {
        }
        
        
    }

    @Override
    public Map<String, String> getNotaryCache(String id) {
        
        try {
            
            return redisClient.getMap(CACHE_KEY_RREFIX + id);
            
        } catch (Exception e) {
            return null;
        }
        
    }

    @Override
    public void setNotaryCache(String id, Map<String, String> sessionDatas) {
        
        try {
            
            id = CACHE_KEY_RREFIX + id;
            redisClient.addMap(id, sessionDatas, cacheTimeout);
            
        } catch (Exception e) {
        }
        
    }

    @Override
    public void removeNotaryCache(String id) {
        
        try {
            
            
            String sid = CACHE_KEY_RREFIX + id;
            redisClient.del(sid);
            
        } catch (Exception e) {
        }
        
    }

    @Override
    public void refresh(String id) {
        
        try {
            
            
            id = CACHE_KEY_RREFIX + id;
            redisClient.expire(id, cacheTimeout);
            
        } catch (Exception e) {
        }
        
    }

    
    @Override
    public String saveDataInRangeTime(String key, String value) {

        return redisClient.getSet(key, value,this.cacheTimeout);
        
    }
    
    @Override
    public String getData(String key) {

        return redisClient.get(key);
        
    }
        


    /**
     * @return the cacheTimeout
     */
    public int getCacheTimeout() {
        return cacheTimeout;
    }

    /**
     * @param cacheTimeout the cacheTimeout to set
     */
    public void setCacheTimeout(int cacheTimeout) {
        this.cacheTimeout = cacheTimeout;
    }

    /**
     * @return the redisClient
     */
    public RedisClient getRedisClient() {
        return redisClient;
    }

    /**
     * @param redisClient the redisClient to set
     */
    public void setRedisClient(RedisClient redisClient) {
        this.redisClient = redisClient;

    }
    
    public void test(){
    System.out.println("test");
    }

    
}
