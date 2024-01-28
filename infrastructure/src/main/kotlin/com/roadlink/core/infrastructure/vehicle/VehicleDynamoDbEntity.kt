package com.roadlink.core.infrastructure.vehicle

import com.roadlink.core.domain.DomainEntity
import com.roadlink.core.domain.vehicle.Vehicle
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoDbEntity
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoDbEntityMapper
import com.roadlink.core.infrastructure.dynamodb.DynamoDbDateFormatter
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.*

class VehicleDynamoDbEntity constructor(
    id: String,
    createdDate: Date,
    var brand: String = "",
    var model: String = "",
    var licencePlate: String = "",
    /** DriverIdGSI */
    var driverId: String = "",
    var iconUrl: String = "",
    var capacity: Int = 0,
) : BaseDynamoDbEntity(id, createdDate) {

    override fun toDomain(): DomainEntity {
        return Vehicle(
            id = UUID.fromString(this.id),
            brand = this.brand,
            model = this.model,
            licencePlate = this.licencePlate,
            driverId = UUID.fromString(this.driverId),
            iconUrl = this.iconUrl,
            capacity = this.capacity
        )
    }


    companion object {

        fun from(item: Map<String, AttributeValue>): VehicleDynamoDbEntity {
            return VehicleDynamoDbEntity(
                id = item["Id"]!!.s(),
                createdDate = DynamoDbDateFormatter.instance().parse(item["CreatedDate"]!!.s()),
                brand = item["Brand"]!!.s(),
                model = item["Model"]!!.s(),
                licencePlate = item["LicencePlate"]!!.s(),
                iconUrl = item["IconUrl"]!!.s(),
                driverId = item["DriverId"]!!.s(),
                capacity = Integer.valueOf(item["Capacity"]!!.n()),
            )
        }
    }
}

class VehicleDynamoDbEntityMapper : BaseDynamoDbEntityMapper<Vehicle, VehicleDynamoDbEntity>() {

    override fun from(item: Map<String, AttributeValue>): VehicleDynamoDbEntity {
        return VehicleDynamoDbEntity.from(item)
    }

    override fun toItem(entity: Vehicle): Map<String, AttributeValue> {
        return mapOf(
            "EntityId" to AttributeValue.builder().s("EntityId#Vehicle").build(),
            "Id" to AttributeValue.builder().s(entity.id.toString()).build(),
            "CreatedDate" to AttributeValue.builder().s(DynamoDbDateFormatter.instance().format(Date())).build(),
            "Brand" to AttributeValue.builder().s(entity.brand).build(),
            "Model" to AttributeValue.builder().s(entity.model).build(),
            "IconUrl" to AttributeValue.builder().s(entity.iconUrl).build(),
            "LicencePlate" to AttributeValue.builder().s(entity.licencePlate).build(),
            "DriverId" to AttributeValue.builder().s(entity.driverId.toString()).build(),
            "Capacity" to AttributeValue.builder().n(entity.capacity.toString()).build()
        )
    }
}