package com.roadlink.core.infrastructure.user

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserRepositoryPort
import java.text.SimpleDateFormat
import java.util.*

class UserRepositoryAdapter(private val mapper: DynamoDBMapper) : UserRepositoryPort {

    private var dateFormatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    init {
        dateFormatter.timeZone = TimeZone.getTimeZone("UTC")
    }

    fun findAll(): List<UserDynamoEntity> {
        val query = DynamoDBScanExpression()
        return mapper.scan(UserDynamoEntity::class.java, query)
    }

    override fun save(user: User): User {
        mapper.save(UserDynamoEntity.from(user))
        return user
    }

//    fun save(book: UserDynamoEntity) {
//        mapper.save(book)
//    }

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

