package commands

import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

@JsonTypeName("clear")
@JacksonXmlRootElement(localName = "clear")
class Clear : Command() {

    override val commandName: String = "clear"
    override fun writeString() {
        println("Очистка коллекции")
    }
    fun execute(connection: Connection) {
        val sql = "TRUNCATE flats RESTART IDENTITY;"
        try {
            val statement = connection.createStatement()
            statement.executeUpdate(sql)
            statement.close()
            println("Таблица успешно очищена, и счетчик сброшен.")
        } catch (e: SQLException) {
            println("Ошибка при выполнении команды очистки таблицы: ${e.message}")
        }
    }
    fun deleteFlatsByUser(connection: Connection, username: String): Boolean {
        val sql = "DELETE FROM flats WHERE user_name = ?"
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, username)
            val rowsAffected = preparedStatement.executeUpdate()
            preparedStatement.close()
            println("Удалены элементы пользователя $username")
            return rowsAffected > 0
        } catch (e: SQLException) {
            println("Ошибка при выполнении команды очистки таблицы: ${e.message}")
            e.printStackTrace()
        }
        return false
    }
}