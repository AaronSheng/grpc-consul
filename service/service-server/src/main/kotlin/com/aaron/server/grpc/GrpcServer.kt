package com.aaron.server.grpc

import com.aaron.server.service.UserService
import com.aaron.server.service.UserServiceGrpc
import io.grpc.Server
import io.grpc.ServerBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

/**
 * Created by Aaron Sheng on 2019/4/28.
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
class GrpcServer @Autowired constructor(
    private val userService: UserService
) {
    @Value("\${grpc.server.port:#{null}}")
    private val port: Int? = null

    private var server: Server? = null

    @PostConstruct
    private fun start() {
        logger.info("Start to listen port($port)")
        if (server == null) {
            server = ServerBuilder.forPort(port!!)
                .addService(userService.bindService())
                .build()
                .start()
        }
    }

    fun shutdown() {
        server?.shutdown()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}