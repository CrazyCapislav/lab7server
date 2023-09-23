package commands

import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

@JsonTypeName("removeLower")
@JacksonXmlRootElement(localName = "removeLower")
class RemoveLower : Command() {

    override val commandName: String = "remove_lower {element}"
    override fun writeString() {
        println("Удалить id ниже чем")
    }

    fun removeLower(connection: Connection, id: Long) {
        val sql = "DELETE FROM flats WHERE id < ?"
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
            preparedStatement.setLong(1, id)
            preparedStatement.executeUpdate()
            preparedStatement.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
    fun removeLowerUser(connection: Connection, id: Long, username: String) {
        val sql = "DELETE FROM flats WHERE id < ? and user_name = ?"
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
            preparedStatement.setLong(1, id)
            preparedStatement.setString(2, username)
            preparedStatement.executeUpdate()
            preparedStatement.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
}