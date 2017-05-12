package br.codesimples.kms

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.encryptionsdk.AwsCrypto
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider

object KmsAdapter {
  def newWithEnvironmentVariables(): KmsAdapter = {
    val keyArn = System.getenv("keyArn")
    val provider = new KmsMasterKeyProvider(new EnvironmentVariableCredentialsProvider(), keyArn)
    KmsAdapter(new AwsCrypto(), provider)
  }
}

case class KmsAdapter(crypto: AwsCrypto, provider: KmsMasterKeyProvider) {

  def crypt(dataPacket: DataPacket): DataPacketResult = {
    val listOfValues = dataPacket.values.map { value =>
      val result = crypto.encryptString(provider, value.value).getResult
      Value(value.attribute, result)
    }
    DataPacketResult(listOfValues)
  }

  def decrypt(dataPacket: DataPacket): DataPacketResult = {
    val listOfValues = dataPacket.values.map { value =>
      val result = crypto.decryptString(provider, value.value).getResult
      Value(value.attribute, result)
    }
    DataPacketResult(listOfValues)
  }

}
