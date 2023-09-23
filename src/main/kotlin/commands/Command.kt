package commands

import AverageOfTimeToMetroOnFoot
import RemoveAllByNumberOfRooms
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "commandType")
@JsonSubTypes(
    JsonSubTypes.Type(value = Help::class, name = "help"),
    JsonSubTypes.Type(value = Add::class, name = "add"),
    JsonSubTypes.Type(value = Show::class, name = "show"),
    JsonSubTypes.Type(value = RemoveById::class, name = "removeById"),
    JsonSubTypes.Type(value = Clear::class, name = "clear"),
    JsonSubTypes.Type(value = UpdateById::class, name = "updateById"),
    JsonSubTypes.Type(value = AddIfMax::class, name = "addIfMax"),
    JsonSubTypes.Type(value = RemoveLower::class, name = "removeLower"),
    JsonSubTypes.Type(value = PrintUniqueTimeToMetroOnFoot::class, name = "print_unique_time_to_metro_on_foot"),
    JsonSubTypes.Type(value = AverageOfTimeToMetroOnFoot::class, name = "average_of_time_to_metro_on_foot"),
    JsonSubTypes.Type(value = Auto::class, name = "auto"),
    JsonSubTypes.Type(value = Register::class, name = "reg"),
    JsonSubTypes.Type(value = RemoveAllByNumberOfRooms::class, name = "remove_all_by_number_of_rooms"),
    JsonSubTypes.Type(value = RemoveGreater::class, name = "removeGreater")
)
abstract class Command {
    abstract val commandName: String
    abstract fun writeString()
    fun writeCommandName(){
        println(commandName)
    }
}
