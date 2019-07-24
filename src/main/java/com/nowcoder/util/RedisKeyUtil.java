package com.nowcoder.util;

public class RedisKeyUtil {

    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";
    private static String BIZ_EVENTQUEUE = "EVENT_QUEUE";
    private static String BIZ_FOLLOWER = "FOLLOWER";
    private static String BIZ_FOLLOWEE = "FOLLOWEE";
    private static String BIZ_TIMELINE = "TIMELINE";
    private static String BIZ_VIEWING = "VIEWING";

    //某一个用户关注的某一个实体队列
    public static String getBizFollowee(int userId, int entityType){
        return BIZ_FOLLOWEE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(userId);
    }

    public static String getViewKey(int questionId){
        return BIZ_VIEWING + SPLIT + String.valueOf(questionId);
    }

    //所有粉丝的队列
    public static String getBizFollower(int entityType, int entityId){
        return BIZ_FOLLOWER + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getLikeKey(int entityType, int entityId){
        return BIZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getDisLikeKey(int entityType, int entityId){
        return BIZ_DISLIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getEventQueue(){
        return BIZ_EVENTQUEUE;
    }

    public static String getTimelineKey(int userId){
        return BIZ_TIMELINE + SPLIT + String.valueOf(userId);
    }
}
