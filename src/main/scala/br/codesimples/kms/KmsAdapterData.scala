package br.codesimples.kms

import java.util.concurrent.Callable

import com.amazonaws.encryptionsdk.AwsCrypto
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider

trait Result {
  def attribute(): String
  def value(): String
  def isFailure: Boolean
}

case class Data(attribute:String, value:String)

case class SuccessResult(attribute:String, value:String) extends Result {
  override def isFailure: Boolean = false
}

case class FailResult() extends Result {
  def attribute(): String = ""
  def value(): String = ""

  override def isFailure: Boolean = true
}

case class DataPacket(values:List[Data])

case class DataPacketResult(values:List[Result])

case class DecryptAction(crypto: AwsCrypto, provider: KmsMasterKeyProvider, data: Data) extends Callable[Result] {
  override def call(): Result = {
    val result = crypto.decryptString(provider, data.value).getResult
    SuccessResult(data.attribute, result)
  }
}

case class EncryptAction(crypto: AwsCrypto, provider: KmsMasterKeyProvider, data: Data) extends Callable[Result] {
  override def call(): Result = {
    val result = crypto.encryptString(provider, data.value).getResult
    SuccessResult(data.attribute, result)
  }
}
