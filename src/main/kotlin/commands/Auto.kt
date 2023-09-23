package commands

import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import models.User

@JsonTypeName("auto")
@JacksonXmlRootElement(localName = "auto")
class Auto : Command(){
    override val commandName: String = "authorize"
    override fun writeString() {
        println("")
    }
    fun execute(login: String, password: String): User {
        return User (login, password)

    }
}