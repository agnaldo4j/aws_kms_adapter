package br.codesimples.kms

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.encryptionsdk.AwsCrypto
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider

object KmsAdapter {
  def main(args: Array[String]): Unit = {
    val crypto = new AwsCrypto()
    val keyArn = System.getenv("keyArn")

    val credentials = new EnvironmentVariableCredentialsProvider()
    val prov = new KmsMasterKeyProvider(credentials, keyArn)

    val ciphertext = crypto.encryptString(prov, "Teste de um texto pequeno para ficar vendo o tamanho do problema").getResult
    System.out.println("Ciphertext: " + ciphertext)

    val decryptResult = crypto.decryptString(prov, ciphertext)
    if (!(decryptResult.getMasterKeyIds.get(0) == keyArn)) throw new IllegalStateException("Wrong key id!")

    System.out.println("Decrypted: " + decryptResult.getResult)
  }
}

class KmsAdapter {
  def crypt() {}

  def decrypt {}
}
