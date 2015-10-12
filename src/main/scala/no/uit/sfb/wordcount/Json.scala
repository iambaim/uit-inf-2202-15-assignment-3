package no.uit.sfb.wordcount

import net.liftweb.json._

object Json {
  implicit val formats = Serialization.formats(NoTypeHints)

  def serialize[A <: AnyRef](obj: A) = Serialization.write(obj)
  def deserialize[A <: AnyRef : Manifest](input: String): A = {
    val json = parse(input)
    json.extract[A]
  }
}
