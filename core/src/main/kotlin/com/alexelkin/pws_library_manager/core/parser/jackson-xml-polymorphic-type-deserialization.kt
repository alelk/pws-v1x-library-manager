import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver
import com.fasterxml.jackson.databind.cfg.MapperConfig
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.jsontype.NamedType
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.XmlTypeResolverBuilder
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.module.kotlin.kotlinModule

val xmlInput = """
  <container>
    <animals>
      <cat name="Vasya"> <age>6</age> </cat>
      <cat name="Murka"> <age>2</age> </cat>
      <dog name="Barbos"/>
      <dog name="Sharik"/>
    <animals>
  </container>
""".trimIndent()

//@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM)
//@JsonSubTypes(
//  JsonSubTypes.Type(value = Cat::class, name = "cat"),
//  JsonSubTypes.Type(value = Dog::class, name = "dog"))
//@JsonTypeResolver(AnimalTypeResolver::class)
//@JsonTypeIdResolver()
@JsonDeserialize(using = AnimalDeserializer::class)
sealed interface Animal

data class Cat(
  @JacksonXmlProperty(isAttribute = true)
  val name: String,
  val age: Int
) : Animal

data class Dog(
  @JacksonXmlProperty(isAttribute = true)
  val name: String
) : Animal

@JacksonXmlRootElement(localName = "container")
data class Container(
//  @JacksonXmlElementWrapper(useWrapping = true, localName = "animals")
  val animals: List<Animal>
)

class AnimalDeserializer: StdDeserializer<Animal>(Animal::class.java) {
  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Animal {
    TODO("Not yet implemented")
  }

}

class AnimalTypeResolver: XmlTypeResolverBuilder() {
  override fun verifyBaseTypeValidity(config: MapperConfig<*>?, baseType: JavaType?): PolymorphicTypeValidator {
    return super.verifyBaseTypeValidity(config, baseType)
  }

  override fun buildTypeDeserializer(
    config: DeserializationConfig?,
    baseType: JavaType?,
    subtypes: MutableCollection<NamedType>?
  ): TypeDeserializer {
    return super.buildTypeDeserializer(config, baseType, subtypes)
  }

  override fun inclusion(includeAs: JsonTypeInfo.As?): StdTypeResolverBuilder {
    return super.inclusion(includeAs)
  }

  override fun getTypeProperty(): String {
    return super.getTypeProperty()
  }
  override fun idResolver(
    config: MapperConfig<*>?,
    baseType: JavaType?,
    subtypeValidator: PolymorphicTypeValidator?,
    subtypes: MutableCollection<NamedType>?,
    forSer: Boolean,
    forDeser: Boolean
  ): TypeIdResolver {
    return super.idResolver(config, baseType, subtypeValidator, subtypes, forSer, forDeser)
  }
}

fun main() {
  val mapper = XmlMapper(JacksonXmlModule()).registerModule(kotlinModule())

  mapper.readValue(xmlInput, Container::class.java)
}