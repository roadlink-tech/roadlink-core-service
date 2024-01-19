package com.roadlink.core.infrastructure.vehicle

import com.roadlink.core.domain.vehicle.VehicleCriteria
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoDbQuery
import com.roadlink.core.infrastructure.dynamodb.DynamoDbQueryMapper
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbException
import java.util.*

class VehicleDynamoDbQuery(
    val id: UUID? = null,
    private val driverId: String = "",
) : BaseDynamoDbQuery() {

    override var entityId: String = "EntityId#Vehicle"

    init {
        if (id == null && driverId == "") {
            throw DynamoDbException.InvalidQuery()
        }
    }

    override fun fieldsInKeyCondition(): List<String> {
        if (driverId != "") {
            return listOf("driverId")
        }
        return listOf("id", "entityId")
    }

    override fun fieldsInFilterExpression(): List<String> {
        val candidates = attributeNames.subtract(fieldsInKeyCondition().toSet()).toMutableList()
        if (id == null) {
            candidates.remove("id")
        }
        if (driverId == "") {
            candidates.remove("driverId")
        }

        return candidates
    }

    override fun indexName(): String {
        if (driverId != "") {
            return "VehicleDriverIdGSI"
        }
        return ""
    }


    companion object {
        fun from(criteria: VehicleCriteria): VehicleDynamoDbQuery {
            return VehicleDynamoDbQuery(
                id = criteria.id,
                driverId = criteria.driverId.toString()
            )
        }
    }
}

class VehicleDynamoDbQueryMapper : DynamoDbQueryMapper<VehicleCriteria, VehicleDynamoDbQuery> {
    override fun from(criteria: VehicleCriteria): VehicleDynamoDbQuery {
        return VehicleDynamoDbQuery.from(criteria)
    }
}


