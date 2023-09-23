package commands


import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import models.Coordinates
import models.Flat
import models.Furnish
import models.House
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

/**
 * Один из классов команд
 */
@JsonTypeName("show")
@JacksonXmlRootElement(localName = "show")
class Show : Command() {

    override val commandName: String = "show"
    override fun writeString() {
        println("Имена элементов коллекции")
    }

    fun showElement(collection: HashSet<Flat>) {
        if (collection.isEmpty()) {
            println("Коллекция пуста")
        } else {
            collection.forEach { el ->
                println(el.toString())
            }
        }
    }

    fun showString(collection: HashSet<Flat>): String {
        val stringBuilder = StringBuilder()

        if (collection.isEmpty()) {
            stringBuilder.append("Коллекция пуста")
        } else {
            val sortedCollection = collection.sortedBy { it.id }
            sortedCollection.forEach { el ->
                stringBuilder.append("id: ${el.id}, username: ${el.username}, date: ${el.creationDate}, name: ${el.name}, coordinates: ${el.coordinates}, livingSpace: ${el.livingSpace}, area: ${el.area}, numberOfRooms: ${el.numberOfRooms}, furnish: ${el.furnish}, house: ${el.house} |||")
            }
        }

        return stringBuilder.toString()
    }

    fun getHashSet(connection: Connection): HashSet<Flat> {
        val flatsSet = HashSet<Flat>()
        val statement: Statement = connection.createStatement()
        val query = "SELECT * FROM flats"
        val resultSet: ResultSet = statement.executeQuery(query)
        while (resultSet.next()) {
            // Вызов next() перед извлечением данных
            val coordinates = Coordinates(resultSet.getLong("x"), resultSet.getLong("y"))
            val furnishValue = resultSet.getString("furnish")
            val furnish = if (furnishValue != null) Furnish.valueOf(furnishValue) else null

            val houseValue = resultSet.getString("house_name")
            val house = if (houseValue != null) {
                House(
                    resultSet.getString("house_name"),
                    resultSet.getLong("house_year"),
                    resultSet.getLong("house_lifts")
                )
            } else null
            val flat = Flat(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                coordinates,
                resultSet.getInt("square"),
                resultSet.getInt("rooms_count"),
                resultSet.getDouble("live_square"),
                resultSet.getInt("metro"),
                furnish,
                house,
                resultSet.getTimestamp("creation_time"),
                resultSet.getString("user_name")
            )
            flatsSet.add(flat)

        }
        resultSet.close()
        statement.close()
        return flatsSet
    }

}