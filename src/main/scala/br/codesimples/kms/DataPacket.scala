package br.codesimples.kms

case class Value(attribute:String, value:String)

case class DataPacket(values:List[Value])

case class DataPacketResult(values:List[Value])

case class KmsAdapterConfig()
