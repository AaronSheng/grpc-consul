package com.aaron.common.grpc

import com.aaron.common.api.exception.ClientException
import com.ecwid.consul.v1.ConsulClient
import com.ecwid.consul.v1.QueryParams
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
/**
 * Created by Aaron Sheng on 2019/4/29.
 */
@Component
class GrpcClient @Autowired constructor(
    private val tag: String?
){
    private val consulClient = ConsulClient()

    fun findServiceInstance(clz: Class<*>): GrpcServiceInstance {
        val serviceName = getServiceName(clz)
        return getServiceInstance(serviceName)
    }

    private fun getServiceInstance(serviceName: String): GrpcServiceInstance {
        val response = consulClient.getHealthServices(serviceName, tag, true, QueryParams.DEFAULT)
        if (response.value.isEmpty()) {
            throw ClientException("找不到任何有效的[$serviceName]服务提供者")
        }

        response.value.shuffle()
        return GrpcServiceInstance(
            response.value.first().service.address,
            response.value.first().service.port
        )
    }

    private fun getServiceName(clz: Class<*>): String {
        val packageName = clz.name
        val regex = Regex("com.aaron.([a-z]+).service.([a-zA-Z]+)")
        val matches = regex.find(packageName) ?: throw ClientException("无法根据接口[$packageName]分析所属的服务")
        return "grpc_${matches.groupValues[1]}"
    }
}