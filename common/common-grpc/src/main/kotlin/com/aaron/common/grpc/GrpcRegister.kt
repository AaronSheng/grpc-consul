package com.aaron.common.grpc

import com.ecwid.consul.v1.ConsulClient
import com.ecwid.consul.v1.agent.model.NewService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.net.InetAddress
import java.util.*
import javax.annotation.PostConstruct

/**
 * Created by Aaron Sheng on 2019/4/29.
 */
@Component
class GrpcRegister @Autowired constructor(
    private val applicationName: String,
    private val grpcPort: Int?,
    private val tags: String?
) {
    // consul client to register service
    private val consulClient = ConsulClient()

    // timer to run registering task
    private val timer = Timer()

    // get local host ip
    private val host =  InetAddress.getLocalHost().hostAddress

    private val defaultRegisterDelay = 15000L
    private val defaultRegisterInterval = 15000L

    @PostConstruct
    fun startAutoRegister() {
        if (grpcPort == null) {
            return
        }

        logger.info("Start to register grpc consul $host:$grpcPort")
        timer.schedule(object : TimerTask() {
            override fun run() {
                val check = NewService.Check()
                check.tcp = "$host:$grpcPort"
                check.interval = "15s"
                check.timeout = "5s"

                val service = NewService()
                service.id = "${applicationName}_${host}_$grpcPort"
                service.name = "grpc_$applicationName"
                service.address = host
                service.port = grpcPort
                service.tags = tags?.split(",")
                service.check = check
                consulClient.agentServiceRegister(service)
            }
        }, defaultRegisterDelay, defaultRegisterInterval)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}