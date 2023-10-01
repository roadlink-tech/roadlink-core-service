package com.roadlink.core.infrastructure.user

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import java.text.SimpleDateFormat
import java.util.*

class DynamoBookRepository(private val mapper: DynamoDBMapper) {

    private var dateFormatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    init {
        dateFormatter.timeZone = TimeZone.getTimeZone("UTC")
    }

    fun findAll(): List<DynamoBook> {
        val query = DynamoDBScanExpression()
        return mapper.scan(DynamoBook::class.java, query)
    }

    fun save(book: DynamoBook) {
        mapper.save(book)
    }

    fun findAllByCategory(category: String): List<DynamoBook> {
        val eav = mutableMapOf<String, AttributeValue>()
        eav[":val1"] = AttributeValue().withS(category.toString())
        val q = DynamoDBQueryExpression<DynamoBook>()
            .withKeyConditionExpression("category = :val1")
            .withExpressionAttributeValues(eav)
        return mapper.query(DynamoBook::class.java, q)
    }

    fun findAllByCreatedDateBefore(date: Date): List<DynamoBook> {
        val mapped = AttributeValue().withS(dateFormatter.format(date))
        val args = Collections.singletonMap(":v1", mapped)
        val query = DynamoDBScanExpression()
            .withIndexName("created_date_idx")
            .withFilterExpression("created_date < :v1")
            .withExpressionAttributeValues(args)
        return mapper.scan(DynamoBook::class.java, query)
    }

    fun findAllByPriceGreaterThan(price: Double): List<DynamoBook> {
        val mapped = AttributeValue().withN(price.toString())
        val args = Collections.singletonMap(":v1", mapped)
        val query = DynamoDBScanExpression()
            .withIndexName("price_idx")
            .withFilterExpression("price > :v1")
            .withExpressionAttributeValues(args)
        return mapper.scan(DynamoBook::class.java, query)
    }

    fun findAllByRatingGreaterThan(rating: Int): List<DynamoBook> {
        val mapped = AttributeValue().withN(rating.toString())
        val args = Collections.singletonMap(":v1", mapped)
        val query = DynamoDBScanExpression()
            .withFilterExpression("rating > :v1")
            .withExpressionAttributeValues(args)
        return mapper.scan(DynamoBook::class.java, query)
    }

    fun findAllByCategoryAndCreatedDateAfter(category: String, date: Date): List<DynamoBook> {
        val eav = mutableMapOf<String, AttributeValue>()
        eav[":v1"] = AttributeValue().withS(dateFormatter.format(date))
        eav[":v2"] = AttributeValue().withS(category.toString())
        val query = DynamoDBScanExpression()
            .withFilterExpression("created_date > :v1 and category = :v2")
            .withExpressionAttributeValues(eav)
        return mapper.scan(DynamoBook::class.java, query)
    }

    fun findAllByCategoryAndPriceGreaterThan(category: String, price: Double): List<DynamoBook> {
        val eav = mutableMapOf<String, AttributeValue>()
        eav[":v1"] = AttributeValue().withN(price.toString())
        eav[":v2"] = AttributeValue().withS(category.toString())
        val query = DynamoDBScanExpression()
            .withFilterExpression("price > :v1 and category = :v2")
            .withExpressionAttributeValues(eav)
        return mapper.scan(DynamoBook::class.java, query)
    }
}