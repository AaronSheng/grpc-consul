package com.aaron.client.service

import com.aaron.client.api.pojo.User
import com.aaron.client.grpc.UserServiceGrpcClient
import com.aaron.common.client.Client
import com.aaron.common.redis.RedisLock
import com.aaron.server.api.UserResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

/**
 * Created by Aaron Sheng on 2019/4/3.
 */
@Service
class UserService @Autowired constructor(
    private val userServiceGrpcClient: UserServiceGrpcClient,
    private val redisTemplate: RedisTemplate<String, String>
) {
    fun getUser(id: Long): User {
        while (true) {
            val redisLock = RedisLock(redisTemplate, "user_$id", 30)
            redisLock.use {
                if (!redisLock.tryLock()) {
                    return@use
                }
                val user = userServiceGrpcClient.getUser(id)
                return User(id, user.name)
            }
        }
    }
}