package br.codesimples.kms

trait Result {
  def attribute(): String
  def value(): String
}

case class Data(attribute:String, value:String)

case class SuccessResult(attribute:String, value:String) extends Result {}

case class FailResult() extends Result {
  def attribute(): String = ""
  def value(): String = ""
}

case class DataPacket(values:List[Data])

case class DataPacketResult(values:List[Result])
