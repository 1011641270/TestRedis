/**
 * This File is created by hztianduoduo at 2016年3月15日,any questions please have
 * a message on me!
 */
package com.tian.redis;

import java.util.Map;

/**
 * @author hztiandd@tian-dd.top 2016年3月15日
 */
public interface NotaryCacheService {

    /**
     * @param id
     * @param key
     * 从缓存中删除一个属性
     */
    void removeNotaryCacheAttribute(String id, String key);

    /**
     * 
     * @param id
     * @param key
     * @param value
     * 在缓存中设置一个属性，如果属性已存在，则更新该值
     */
    void putNotaryCacheAttribute(String id, String key, String value);

    /**
     * 
     * @param id
     * @return
     * 从缓存中取数据
     */
    Map<String, String> getNotaryCache(String id);
    
    /**
     * 
     * @param id
     * @param sessionDatas
     */
    void setNotaryCache(String id, Map<String, String> sessionDatas);

    /**
     * 
     * @param id
     * 移除缓存
     */
    void removeNotaryCache(String id);

    /**
     * 
     * @param id
     * 刷新缓存
     */
    void refresh(String id);
    
    String saveDataInRangeTime(final String key, final String value);
    
    String getData(final String key);
    
}
