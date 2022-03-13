package com.example.relationaldataaccess

import mu.*
import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.context.annotation.*
import org.springframework.jdbc.core.*
import java.sql.*

@SpringBootApplication
class RelationalDataAccessApplication(val jdbcTemplate: JdbcTemplate) {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @Bean
    fun run(): CommandLineRunner {
        return CommandLineRunner {
            logger.info { "Creating tables" }

            jdbcTemplate.execute("DROP TABLE customers IF EXISTS ")
            jdbcTemplate.execute("CREATE TABLE customers(id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))")

            listOf("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long").forEach {
                    val fullNameByList = it.split(" ")
                    val fistName = fullNameByList[0]
                    val lastName = fullNameByList[1]
                    logger.info { "Inserting customer record $fistName $lastName" }
                    jdbcTemplate.execute("INSERT INTO customers(first_name, last_name) VALUES('$fistName', '$lastName')")
                }

            logger.info { "Querying for customer records where first_name = 'Josh':" }

            val rowMapper: RowMapper<Customer> = RowMapper<Customer> { rs: ResultSet, _: Int ->
                Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"))
            }
            jdbcTemplate.query("SELECT id, first_name, last_name FROM customers Where first_name = 'Josh'", rowMapper)
                .forEach { logger.info { it } }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<RelationalDataAccessApplication>(*args)
}