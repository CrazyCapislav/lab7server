import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import commands.Command
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

@JsonTypeName("remove_all_by_number_of_rooms")
@JacksonXmlRootElement(localName = "remove_all_by_number_of_rooms")
class RemoveAllByNumberOfRooms : Command() {

    override val commandName: String = "remove_all_by_number_of_rooms"
    override fun writeString() {
        println("Удалить все с числом комнат")
    }

    fun removeByRooms(connection: Connection, numberOfRooms: Int) {
        val sql = "DELETE FROM flats WHERE rooms_count = ?"
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
            preparedStatement.setInt(1, numberOfRooms)
            preparedStatement.executeUpdate()
            preparedStatement.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
    fun removeByRoomsAndUsername(connection: Connection, numberOfRooms: Int, username: String) {
        val sql = "DELETE FROM flats WHERE rooms_count = ? AND user_name = ?"
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
            preparedStatement.setInt(1, numberOfRooms)
            preparedStatement.setString(2, username)
            preparedStatement.executeUpdate()
            preparedStatement.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
}