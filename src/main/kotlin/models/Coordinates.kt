package models
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "coordinates")
class Coordinates (
    @JacksonXmlProperty(localName = "coordinatesX")
    val x: Long = 0,
    @JacksonXmlProperty(localName = "coordinatesY")
    val y: Long = 0
) {
    override fun toString(): String {
        return "Coordinates(x=$x, y=$y)"
    }
}
