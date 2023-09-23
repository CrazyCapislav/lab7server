package commands

import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import models.Flat
import models.User
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Timestamp

@JacksonXmlRootElement(localName = "Add")
@JsonTypeName("add")
class Add : Command() {
    override val commandName: String = "add {element}"
    override fun writeString() {
        println("")
    }
    fun execute(connection: Connection, flat: Flat, username: String) {
        val sql =
            "INSERT INTO flats (creation_time, name, x, y, square, rooms_count, live_square, metro, furnish, house_name, house_year, house_lifts, user_name) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
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
    }
}
