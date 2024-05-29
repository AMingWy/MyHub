package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * ClassName: FollowService
 * Package: com.nowcoder.community.service
 * Description:
 *
 * @Autuor Ming Zhang
 * @Version 1.0
 */
@Service
public class FollowService implements CommunityConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    /**
     * Follow.
     * 关注的业务逻辑
     *
     * @param userId     the user id
     * @param entityType the entity type
     * @param entityId   the entity id
     * @throws InterruptedException the interrupted exception
     */
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                operations.multi();

                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());

                return operations.exec();
            }
        });
    }

    /**
     * Un follow.
     * 取消关注的业务逻辑
     *
     * @param userId     the user id
     * @param entityType the entity type
     * @param entityId   the entity id
     * @throws InterruptedException the interrupted exception
     */
    public void unFollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                operations.multi();

                operations.opsForZSet().remove(followeeKey, entityId);
                operations.opsForZSet().remove(followerKey, userId);

                return operations.exec();
            }
        });
    }

    /**
     * Find followee count long.
     * 查询关注的实体的数量
     *
     * @param userId     the user id
     * @param entityType the entity type
     * @return the long
     */
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    /**
     * Find follower count long.
     * 查询实体的粉丝的数量
     *
     * @param entityType the entity type
     * @param entityId   the entity id
     * @return the long
     */
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    /**
     * Has followed boolean.
     * 查询当前用户是否已关注该实体
     *
     * @param userId     the user id
     * @param entityType the entity type
     * @param entityId   the entity id
     * @return the boolean
     */
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    /**
     * Find followees list.
     * 查询某个用户关注的人
     *
     * @param userId the user id
     * @param offset the offset
     * @param limit  the limit
     * @return the list
     */
    public List<Map<String, Object>> findFollowees(int userId, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset+limit-1);

        if (targetIds == null) {
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for(Integer targetId : targetIds) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }

        return list;
    }

    /**
     * Find followers list.
     * 查询某个用户的粉丝
     *
     * @param userId the user id
     * @param offset the offset
     * @param limit  the limit
     * @return the list
     */
    public List<Map<String, Object>> findFollowers(int userId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset+limit-1);

        if(targetIds == null) {
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for(Integer targetId : targetIds) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followerKey, targetId);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }

        return list;
    }


}
