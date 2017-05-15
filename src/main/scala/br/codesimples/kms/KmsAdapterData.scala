package br.codesimples.kms

import java.util.concurrent.Callable

import com.amazonaws.encryptionsdk.AwsCrypto
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider

trait Operation {
  def isEncrypt(): Boolean
  def toString(): String
}

case class Encrypt() extends Operation {
  def isEncrypt(): Boolean = true
  override def toString(): String = "encrypt"
}
case class Decrypt() extends Operation {
  def isEncrypt(): Boolean = false
  override def toString(): String = "decrypt"
}

trait Result {
  def attribute(): String
  def value(): String
  def isFailure: Boolean
  def operation: Operation
}

case class Data(attribute: String, value: String)

case class SuccessResult(attribute: String, value: String, operation: Operation) extends Result {
  override def isFailure: Boolean = false
}

case class FailResult(operation:Operation) extends Result {
  def attribute(): String = ""
  def value(): String = ""

  override def isFailure: Boolean = true
}

case class DataPacket(values: List[Data]) {
  def addData(attribute:String, value: String): DataPacket = {
    DataPacket( values ++ List[Data]( Data(attribute, value)) )
  }
}

case class DataPacketResult(values: List[Result])

case class DecryptAction(crypto: AwsCrypto, provider: KmsMasterKeyProvider, data: Data) extends Callable[Result] {
  override def call(): Result = {
    val result = crypto.decryptString(provider, data.value).getResult
    SuccessResult(data.attribute, result, Decrypt())
  }
}

case class EncryptAction(crypto: AwsCrypto, provider: KmsMasterKeyProvider, data: Data) extends Callable[Result] {
  override def call(): Result = {
    val result = crypto.encryptString(provider, data.value).getResult
    SuccessResult(data.attribute, result, Encrypt())
  }
}
