package br.codesimples.kms

import java.util.concurrent._

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.encryptionsdk.AwsCrypto
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider

import scala.collection.JavaConverters
import scala.collection.JavaConverters._

object KmsAdapter {
  val EXECUTOR_SERVICE = Executors.newCachedThreadPool()

  def newWithEnvironmentVariables(): KmsAdapter = {
    val keyArn = System.getenv("keyArn")
    val provider = new KmsMasterKeyProvider(new EnvironmentVariableCredentialsProvider(), keyArn)

    KmsAdapter(new AwsCrypto(), provider, EXECUTOR_SERVICE)
  }
}

case class KmsAdapter(crypto: AwsCrypto, provider: KmsMasterKeyProvider, executorService: ExecutorService) {

  def crypt(dataPacket: DataPacket): DataPacketResult = {

    val listOfValues = dataPacket.values.map { value =>
      val result = crypto.encryptString(provider, value.value).getResult
      SuccessResult(value.attribute, result)
    }
    DataPacketResult(listOfValues)
  }

  def decrypt(dataPacket: DataPacket): DataPacketResult = {
    val listOfActions = dataPacket.values.map { data => DecryptAction(crypto, provider, data) }.asJava
    val listOfFutures =  invokeAllDecryptActions(listOfActions)

    val listOfResults = listOfFutures.map{ future =>
      if (future.isDone) future.get()
      else FailResult
    }
    DataPacketResult(listOfResults.asInstanceOf[List[Result]])
  }

  private def invokeAllDecryptActions(listOfActions: java.util.List[DecryptAction]): List[Future[Result]] = {
    executorService.invokeAll(listOfActions, TimeUnit.SECONDS.toSeconds(30), TimeUnit.SECONDS).asScala.toList
  }
}

case class DecryptAction(crypto: AwsCrypto, provider: KmsMasterKeyProvider, data: Data) extends Callable[Result] {
  override def call(): Result = {
    val result = crypto.decryptString(provider, data.value).getResult
    SuccessResult(data.attribute, result)
  }
}
