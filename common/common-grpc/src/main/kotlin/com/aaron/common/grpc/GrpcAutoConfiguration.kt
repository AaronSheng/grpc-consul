package com.aaron.common.grpc

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered

/**
 * Created by Aaron Sheng on 2019/4/29.
 */
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
class GrpcAutoConfiguration {
    @Value("\${spring.application.name:#{null}}")
    private val applicationName: String? = null

    @Value("\${grpc.server.port:#{null}}")
    private val grpcPort: Int? = null

    @Value("\${grpc.consul.discovery.tags:#{null}}")
    private val grpcConsulTags: String = "grpc"

    @Bean
    fun grpcRegister() = GrpcRegister(applicationName!!, grpcPort, grpcConsulTags)

    @Bean
    fun grpcClient() = GrpcClient(grpcConsulTags)
}