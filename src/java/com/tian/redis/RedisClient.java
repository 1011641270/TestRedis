/**
 * This File is created by hztianduoduo at 2016年3月15日,any questions please have a message on me!
 */
package com.tian.redis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.StringUtils;

/**
 * @author hztiandd@tian-dd.top
 * 
 * 2016年3月15日
 */
public class RedisClient {
    
    private final static byte[] LOCK_VALUE = "lock".getBytes();
    
    private StringRedisTemplate redisTemplate;

    /**
     * 设置一个键值对，如果key存在，覆盖旧值，并返回旧值（旧值不存在则返回null）
     * 
     * @param key
     * @param value
     * @param expire
     *            缓存时长（秒），大于0有效
     * */
    public String getSet(final String key, final String value,final Integer expire) {
            return redisTemplate.execute(new RedisCallback<String>() {
                    @Override
                    public String doInRedis(RedisConnection connection)
                                    throws DataAccessException {
                            RedisSerializer<String> serializer = redisTemplate
                                            .getStringSerializer();
                            byte[] dat = connection.getSet(serializer.serialize(key),
                                            serializer.serialize(value));
                            if (expire != null && expire > 0) {
                                    redisTemplate.expire(key, expire, TimeUnit.SECONDS);
                            }
                            if (dat == null) {
                                    return null;
                            }
                            return serializer.deserialize(dat);
                    }
            });
    }
    
    
    /**
     * 缓存中key的数量
     */
    public Long dbsize() {
            return redisTemplate.execute(new RedisCallback<Long>() {
                    @Override
                    public Long doInRedis(RedisConnection connection)
                                    throws DataAccessException {
                            return connection.dbSize();
                    }
            });
    }
  
    
    /**
     * 删除db中的所有key
     */
    public void flushDb() {
            redisTemplate.execute(new RedisCallback<Object>() {
                    @Override
                    public Object doInRedis(RedisConnection connection)
                                    throws DataAccessException {
                            connection.flushDb();
                            return null;
                    }
            });
    }
    
    
    /**
     * key对应的map中添加一条记录，如果该记录的key值已存在就覆盖
     * */
    public boolean setMapItem(final String key, final String field,
                    final String value) {
            return redisTemplate.execute(new RedisCallback<Boolean>() {
                    @Override
                    public Boolean doInRedis(RedisConnection connection) {
                            RedisSerializer<String> serializer = redisTemplate
                                            .getStringSerializer();

                            return connection.hSet(serializer.serialize(key),
                                            serializer.serialize(field),
                                            serializer.serialize(value));
                    }
            });
    }
    
    
    /**
     * 对一个key加锁，如果这个key已存在，则加锁失败
     * 
     * */
    public boolean lock(String key, final Integer expire) {
            final String lockkey = "lock_" + key + "_lock";
            return redisTemplate.execute(new RedisCallback<Boolean>() {
                    @Override
                    public Boolean doInRedis(RedisConnection connect)
                                    throws DataAccessException {
                            RedisSerializer<String> serializer = redisTemplate
                                            .getStringSerializer();
                            byte[] key = serializer.serialize(lockkey);
                            try {
                                    if (expire != null && expire > 0) {
                                            connect.setEx(key, expire, LOCK_VALUE);
                                    } else {
                                            return connect.setNX(key, LOCK_VALUE);
                                    }
                                    return true;
                            } catch (Exception e) {
                            }
                            return false;
                    }
            }).booleanValue();
    }
    
    /**
     * 解除对key的锁定
     * */
    public boolean unlock(final String key) {
            final String lockkey = "lock_" + key + "_lock";
            return redisTemplate.execute(new RedisCallback<Boolean>() {
                    @Override
                    public Boolean doInRedis(RedisConnection connect)
                                    throws DataAccessException {
                            RedisSerializer<String> serializer = redisTemplate
                                            .getStringSerializer();
                            try {
                                    byte[] key = serializer.serialize(lockkey);
                                    connect.del(key);
                                    return true;
                            } catch (Exception e) {
                            }
                            return false;
                    }
            });
    }
    
    /**
     * 获取全局的自增ID ,ID 从1开始
     * */
    public long uniqId(final String key) {
            return redisTemplate.execute(new RedisCallback<Long>() {
                    public Long doInRedis(RedisConnection connect)
                                    throws DataAccessException {
                            RedisSerializer<String> serializer = redisTemplate
                                            .getStringSerializer();
                            long id = connect.incr(serializer.serialize(key)) + 1;
                            if ((id + 65536) >= Long.MAX_VALUE) {
                                    connect.set(serializer.serialize(key),
                                                    serializer.serialize("1"));
                            }
                            return id;
                    }
            });
    }
    
    
    /**
     * @return the redisTemplate
     */
    public StringRedisTemplate getRedisTemplate() {
            return redisTemplate;
    }

    /**
     * @param redisTemplate the redisTemplate to set
     */
    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
            this.redisTemplate = redisTemplate;
    }


    /**
     * @param id
     * @param key
     */
    public Long delMapItem(final String key, final String... fields) {
            if (fields == null || fields.length <= 0) {
                    return 0L;
            }
            return redisTemplate.execute(new RedisCallback<Long>() {
                    public Long doInRedis(RedisConnection connect)
                                    throws DataAccessException {
                            RedisSerializer<String> serializer = redisTemplate
                                            .getStringSerializer();
                            byte[][] bfield = new byte[fields.length][];
                            for (int i = 0; i < fields.length; i++) {
                                    bfield[i] = serializer.serialize(fields[i]);
                            }

                            return connect.hDel(serializer.serialize(key), bfield);
                    }
            });
    }
    
    /**
     * 设置一个key的空闲过期时间
     * 
     * @param id
     * @param expire
     *      过期时间，秒
     */
    public void expire(String key, int expire) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }


    /**
     * @param sid
     * @return
     */
    public void del(String key) {
            redisTemplate.delete(key);
    }


    /**
     * @param string
     * @return
     */
    public Map<String, String> getMap(final String key) {
            return redisTemplate.execute(new RedisCallback<Map<String, String>>() {
                    public Map<String, String> doInRedis(RedisConnection connect)
                                    throws DataAccessException {
                            RedisSerializer<String> serializer = redisTemplate
                                            .getStringSerializer();
                            Map<byte[], byte[]> ret = connect.hGetAll(serializer
                                            .serialize(key));
                            
                            if (ret != null && ret.size() > 0) {
                                    Map<String, String> map = new HashMap<String, String>();
                                    for (Entry<byte[], byte[]> entry : ret.entrySet()) {
                                            map.put(serializer.deserialize(entry.getKey()),
                                                            serializer.deserialize(entry.getValue()));
                                    }
                                    return map;
                            }
                            return null;
                    }
            });
    }
    


    /**
     * @param key
     * @param data
     * @param timeout
     */
    public void addMap(final String key, final Map<String, String> data,
                    int timeout) {
            if (data == null || data.size() <= 0) {
                    return;
            }
            redisTemplate.execute(new RedisCallback<Object>() {
                    public Object doInRedis(RedisConnection connect)
                                    throws DataAccessException {
                            RedisSerializer<String> serializer = redisTemplate
                                            .getStringSerializer();
                            Map<byte[], byte[]> map = new HashMap<byte[], byte[]>();
                            for (Entry<String, String> entry : data.entrySet()) {
                                    map.put(serializer.serialize(entry.getKey()),
                                                    serializer.serialize(entry.getValue()));
                            }
                            connect.hMSet(serializer.serialize(key), map);
                            return null;
                    }
            });
    }


    /**
     * 从map中获取对应值
     * 
     * @param key
     * @param field
     * @return
     */
    public String getMapItem(final String key, final String field) {
            if (StringUtils.isEmpty(key) || StringUtils.isEmpty(field)) {
                    return null;
            }
            return redisTemplate.execute(new RedisCallback<String>() {
                    public String doInRedis(RedisConnection connect) {
                            RedisSerializer<String> serializer = redisTemplate
                                            .getStringSerializer();
                            byte[] ret = connect.hGet(serializer.serialize(key),
                                            serializer.serialize(field));
                            if (ret == null || ret.length <= 0) {
                                    return null;
                            }
                            return serializer.deserialize(ret);
                    }
            });
    }


    /**
     * 获取key对应的value
     * 
     * @param key
     * @return
     */
    public String get(final String key) {
            return redisTemplate.execute(new RedisCallback<String>() {
                    public String doInRedis(RedisConnection connect) {
                            RedisSerializer<String> serializer = redisTemplate
                                            .getStringSerializer();
                            byte[] ret = connect.get(serializer.serialize(key));
                            if (ret == null || ret.length <= 0) {
                                    return null;
                            }
                            return serializer.deserialize(ret);
                    }
            });
    }


    /**
     * key是否存在
     * 
     * @param string
     * @return
     */
    public boolean exist(final String key) {
            return redisTemplate.execute(new RedisCallback<Boolean>() {
                    public Boolean doInRedis(RedisConnection connect) {
                            RedisSerializer<String> serializer = redisTemplate
                                            .getStringSerializer();
                            return connect.exists(serializer.serialize(key));
                    }
            });
    }
    
    /**
     * 获取key对应的value
     * 
     * @param key
     * @return
     */
    public Set<String> list(final String keyPatts) {
            return redisTemplate.execute(new RedisCallback<Set<String>>() {
                    public Set<String> doInRedis(RedisConnection connect) {
                            RedisSerializer<String> serializer = redisTemplate
                                            .getStringSerializer();
                            Set<byte[]> ret = connect.keys(serializer.serialize(keyPatts));
                            if (ret == null || ret.size() <= 0) {
                                    return null;
                            }

                            Set<String> set = new HashSet<String>(ret.size());
                            for (byte[] bs : ret) {
                                    set.add(serializer.deserialize(bs));
                            }
                            return set;
                    }
            });
    }


}
