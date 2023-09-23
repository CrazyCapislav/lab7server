package commands

import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import models.User

@JsonTypeName("reg")
@JacksonXmlRootElement(localName = "reg")
class Register : Command(){
    override val commandName: String = "register"
    override fun writeString() {
        println("")
    }
}