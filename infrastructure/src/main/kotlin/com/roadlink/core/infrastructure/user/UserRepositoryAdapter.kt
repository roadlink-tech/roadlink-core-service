package com.roadlink.core.infrastructure.user

import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.user.UserRepositoryPort
import com.roadlink.core.infrastructure.user.error.UserInfrastructureError
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.QueryRequest

class UserRepositoryAdapter(
    private val dynamoDbClient: DynamoDbClient,
    private val tableName: String = "RoadlinkCore"
) : UserRepositoryPort {

    override fun save(user: User): User {
        val item = UserDynamoEntity.toItem(user)
        val putItemRequest = PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .build()
        dynamoDbClient.putItem(putItemRequest).also { return user }
    }

    override fun findOrFail(criteria: UserCriteria): User {
        val userDynamoCriteria = UserDynamoCriteria.from(criteria)
        val query = buildQuery(
            userDynamoCriteria.keyConditionExpression(),
            userDynamoCriteria.expressionAttributeValues()
        )
        val queryResponse = dynamoDbClient.query(query)
        if (queryResponse.items().isEmpty()) {
            throw UserInfrastructureError.NotFound(userDynamoCriteria.keyConditionExpression())
        }

        val users: MutableList<UserDynamoEntity> = ArrayList()

        queryResponse.items().forEach { item ->
            users.add(UserDynamoEntity.from(item))
        }

        return users.first().toDomain()
    }

    private fun buildQuery(
        conditionExpression: String,
        expressionAttributeValues: Map<String, AttributeValue>
    ): QueryRequest {
        if (expressionAttributeValues[":email"] != null) {
            return QueryRequest.builder()
                .indexName("EmailLSI")
                .tableName(tableName)
                .keyConditionExpression(conditionExpression)
                .expressionAttributeValues(expressionAttributeValues)
                .build()
        }
        return QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression(conditionExpression)
            .expressionAttributeValues(expressionAttributeValues)
            .build()
    }


//    fun findAllByCategory(category: String): List<UserDynamoEntity> {
//        val eav = mutableMapOf<String, AttributeValue>()
//        eav[":val1"] = AttributeValue().withS(category.toString())
//        val q = DynamoDBQueryExpression<UserDynamoEntity>()
//            .withKeyConditionExpression("category = :val1")
//            .withExpressionAttributeValues(eav)
//        return mapper.query(UserDynamoEntity::class.java, q)
//    }
//
//    fun findAllByCreatedDateBefore(date: Date): List<UserDynamoEntity> {
//        val mapped = AttributeValue().withS(dateFormatter.format(date))
//        val args = Collections.singletonMap(":v1", mapped)
//        val query = DynamoDBScanExpression()
//            .withIndexName("created_date_idx")
//            .withFilterExpression("created_date < :v1")
//            .withExpressionAttributeValues(args)
//        return mapper.scan(UserDynamoEntity::class.java, query)
//    }
//
//    fun findAllByPriceGreaterThan(price: Double): List<UserDynamoEntity> {
//        val mapped = AttributeValue().withN(price.toString())
//        val args = Collections.singletonMap(":v1", mapped)
//        val query = DynamoDBScanExpression()
//            .withIndexName("price_idx")
//            .withFilterExpression("price > :v1")
//            .withExpressionAttributeValues(args)
//        return mapper.scan(UserDynamoEntity::class.java, query)
//    }
//
//    fun findAllByRatingGreaterThan(rating: Int): List<UserDynamoEntity> {
//        val mapped = AttributeValue().withN(rating.toString())
//        val args = Collections.singletonMap(":v1", mapped)
//        val query = DynamoDBScanExpression()
//            .withFilterExpression("rating > :v1")
//            .withExpressionAttributeValues(args)
//        return mapper.scan(UserDynamoEntity::class.java, query)
//    }
//
//    fun findAllByCategoryAndCreatedDateAfter(category: String, date: Date): List<UserDynamoEntity> {
//        val eav = mutableMapOf<String, AttributeValue>()
//        eav[":v1"] = AttributeValue().withS(dateFormatter.format(date))
//        eav[":v2"] = AttributeValue().withS(category.toString())
//        val query = DynamoDBScanExpression()
//            .withFilterExpression("created_date > :v1 and category = :v2")
//            .withExpressionAttributeValues(eav)
//        return mapper.scan(UserDynamoEntity::class.java, query)
//    }
//
//    fun findAllByCategoryAndPriceGreaterThan(category: String, price: Double): List<UserDynamoEntity> {
//        val eav = mutableMapOf<String, AttributeValue>()
//        eav[":v1"] = AttributeValue().withN(price.toString())
//        eav[":v2"] = AttributeValue().withS(category.toString())
//        val query = DynamoDBScanExpression()
//            .withFilterExpression("price > :v1 and category = :v2")
//            .withExpressionAttributeValues(eav)
//        return mapper.scan(UserDynamoEntity::class.java, query)
//    }
}

