package com.threads.feedservice.service.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.threads.feedservice.dto.FeedItemDto;
import com.threads.feedservice.dto.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
@RequiredArgsConstructor
public class FeedCacheService {

    private final JedisPool jedisPool;
    private final ObjectMapper mapper;

    private static final int CACHE_TTL_SECONDS = 150;
    private static final String FEED_CACHE_KEY_PREFIX = "feed:";

    public void cacheFeed(Long userId, int page, PageDto<FeedItemDto> feedPage) {
        String cacheKey = buildCacheKey(userId, page);
        try (Jedis jedis = jedisPool.getResource()) {
            String jsonFeed = mapper.writeValueAsString(feedPage);
            jedis.setex(cacheKey, CACHE_TTL_SECONDS, jsonFeed);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public PageDto<FeedItemDto> getCachedFeed(Long userId, int page) {
        String cacheKey = buildCacheKey(userId, page);
        try (Jedis jedis = jedisPool.getResource()) {
            String jsonFeed = jedis.get(cacheKey);
            if (jsonFeed != null) {
                return mapper.readValue(jsonFeed, new TypeReference<>() {});
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void cacheTrending(PageDto<FeedItemDto> dtoPage) {
        String cacheKey = "trending";
        try (Jedis jedis = jedisPool.getResource()) {
            String jsonFeed = mapper.writeValueAsString(dtoPage);
            jedis.setex(cacheKey, 2 * 60, jsonFeed); //30 * 60
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public PageDto<FeedItemDto> getCachedTrending() {
        String cacheKey = "trending";
        try(Jedis jedis = jedisPool.getResource()) {
            String jsonFeed = jedis.get(cacheKey);
            if (jsonFeed != null) {
                return mapper.readValue(jsonFeed, new TypeReference<>() {});
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void evictFeed(Long userId, int page) {
        try (Jedis jedis = jedisPool.getResource()) {
            String cacheKey = buildCacheKey(userId, page);
            jedis.del(cacheKey);
        }
    }

    private String buildCacheKey(Long userId, int page) {
        return FEED_CACHE_KEY_PREFIX + userId + "_" + page;
    }
}
