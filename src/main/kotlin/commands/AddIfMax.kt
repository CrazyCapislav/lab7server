package commands

import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import models.Flat
import java.sql.*

@JsonTypeName("addIfMax")
@JacksonXmlRootElement(localName = "addIfMax")
class AddIfMax : Command() {
    override val commandName: String = "add_if_max {element}"
    override fun writeString() {
        println("Добавить элемент если id больше максимального")
    }
    fun execute(connection: Connection, flat: Flat, username:String) {
        val maxId = getMaxFlatId(connection)
        if (flat.id > maxId) {
            val sql = "INSERT INTO flats (creation_time, name, x, y, square, rooms_count, live_square, metro, furnish, house_name, house_year, house_lifts, user_name) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            try {
                val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
                preparedStatement.setTimestamp(1, flat.creationDate)
                preparedStatement.setString(2, flat.name)
                preparedStatement.setLong(3, flat.coordinates.x)
                preparedStatement.setLong(4, flat.coordinates.y)
                preparedStatement.setInt(5, flat.area)
                preparedStatement.setInt(6, flat.numberOfRooms)
                preparedStatement.setDouble(7, flat.livingSpace)
                preparedStatement.setInt(8, flat.timeToMetroOnFoot)
                preparedStatement.setString(9, flat.furnish?.name)
                preparedStatement.setString(10, flat.house?.name)
                preparedStatement.setLong(11, flat.house?.year ?: 0)
                preparedStatement.setLong(12, flat.house?.numberOfLifts?: 0)
                preparedStatement.setString(13, username)
                preparedStatement.executeUpdate()
                preparedStatement.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
    }

    private fun getMaxFlatId(connection: Connection): Long {
        var maxId: Long = 0

        val sql = "SELECT MAX(id) AS max_id FROM flats"
        try {
            val statement: Statement = connection.createStatement()
            val resultSet: ResultSet = statement.executeQuery(sql)

            if (resultSet.next()) {
                maxId = resultSet.getLong("max_id")
            }

            resultSet.close()
            statement.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return maxId
    }
}