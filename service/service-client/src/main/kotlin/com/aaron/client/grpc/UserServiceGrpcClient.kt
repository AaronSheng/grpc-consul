package com.aaron.client.grpc

import com.aaron.client.api.pojo.User
import com.aaron.common.grpc.GrpcClient
import com.aaron.server.service.UserRequest
import com.aaron.server.service.UserServiceGrpc
import io.grpc.ManagedChannelBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by Aaron Sheng on 2019/4/29.
 */
@Service
class UserServiceGrpcClient @Autowired constructor(
    private val grpcClient: GrpcClient
) {
    fun getUser(userId: Long): User {
        val instance = grpcClient.findServiceInstance(UserServiceGrpc::class.java)
        val channel = ManagedChannelBuilder
            .forAddress(instance.host, instance.port)
            .usePlaintext()
            .build()
        val stub = UserServiceGrpc.newBlockingStub(channel)

        val request = UserRequest.newBuilder().setUserId(userId).build()
        val response = stub.getUser(request)
        return User(response.userId, response.name)
    }
}