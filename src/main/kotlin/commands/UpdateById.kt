package commands

import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import models.*
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

@JsonTypeName("updateById")
@JacksonXmlRootElement(localName = "updateById")
class UpdateById : Command() {
    override val commandName: String = "update id {element}"
    override fun writeString() {
        println("Изменение элемента")
    }
    fun update(connection: Connection, flat: Flat, id: Long){
        val sql = "UPDATE flats SET " +
                "creation_time = ?, " +
                "name = ?, " +
                "x = ?, " +
                "y = ?, " +
                "square = ?, " +
                "rooms_count = ?, " +
                "live_square = ?, " +
                "metro = ?, " +
                "furnish = ?, " +
                "house_name = ?, " +
                "house_year = ?, " +
                "house_lifts = ? " +
                "WHERE id = ?"

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
            preparedStatement.setLong(13, id)
            preparedStatement.executeUpdate()
            preparedStatement.close()

        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
    fun updateUser(connection: Connection, flat: Flat, id: Long, username: String){
        val sql = "UPDATE flats SET " +
                "creation_time = ?, " +
                "name = ?, " +
                "x = ?, " +
                "y = ?, " +
                "square = ?, " +
                "rooms_count = ?, " +
                "live_square = ?, " +
                "metro = ?, " +
                "furnish = ?, " +
                "house_name = ?, " +
                "house_year = ?, " +
                "house_lifts = ? " +
                "WHERE id = ? and user_name = ?"

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
            preparedStatement.setLong(13, id)
            preparedStatement.setString(14, username)
            preparedStatement.executeUpdate()
            preparedStatement.close()

        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
}