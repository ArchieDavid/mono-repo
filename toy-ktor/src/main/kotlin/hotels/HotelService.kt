package hotels

import Hotels
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

interface HotelService {
    suspend fun create(name: String, age: Int?): Int
    suspend fun all(): List<Hotel>
    suspend fun findById(id: Int): Hotel?
    suspend fun deleteById(id: Int)
    suspend fun update(id: Int, name: String, age: Int?): Int
}

class HotelServiceDB : HotelService {
    override suspend fun create(name: String, age: Int?): Int {
        val id = transaction {
            Hotels.insertAndGetId { hotel ->
                hotel[Hotels.name] = name
                if (age != null) {
                    hotel[Hotels.age] = age
                }
            }
        }
        return id.value
    }

    override suspend fun all(): List<Hotel> {
        return transaction {
            Hotels.selectAll().map { row ->
                row.asHotel()
            }
        }
    }

    override suspend fun findById(id: Int): Hotel? {
        val row = transaction {
            addLogger(StdOutSqlLogger)
            Hotels.select {
                Hotels.id eq id
            }.firstOrNull()
        }
        return row?.asHotel()
    }

    override suspend fun deleteById(id: Int) {
        transaction {
            addLogger(StdOutSqlLogger)
            Hotels.deleteWhere {
                Hotels.id eq id
            }
        }
    }

    override suspend fun update(id: Int, name: String, age: Int?):Int {
        return transaction {
            addLogger(StdOutSqlLogger)
            Hotels.update({ Hotels.id eq id }) {
                it[Hotels.name] = name
                it[Hotels.age] = age!!
            }
        }

    }
}

private fun ResultRow.asHotel(): Hotel = Hotel(
    this[Hotels.id].value,
    this[Hotels.name],
    this[Hotels.age]
)

data class Hotel(val id: Int, val name: String, val age: Int)

