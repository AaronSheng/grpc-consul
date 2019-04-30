package com.aaron.server.service

import com.aaron.server.dao.UserDao
import io.grpc.stub.StreamObserver
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by Aaron Sheng on 2018/6/19.
 */
@Service
class UserService @Autowired constructor(
    private val dslContext: DSLContext,
    private val userDao: UserDao
): UserServiceGrpc.UserServiceImplBase() {

    override fun getUser(request: UserRequest, responseObserver: StreamObserver<UserResponse>) {
        logger.info("Get user(${request.userId})")
        val user = userDao.get(dslContext, request.userId)
        val userResponse =  UserResponse.newBuilder()
            .setUserId(user.id)
            .setName(user.name)
            .build()
        responseObserver.onNext(userResponse)
        responseObserver.onCompleted()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}