package br.codesimples.kms

import java.util.concurrent._

import com.amazonaws.auth.{BasicAWSCredentials, EnvironmentVariableCredentialsProvider}
import com.amazonaws.encryptionsdk.AwsCrypto
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider

import scala.collection.JavaConverters._

object KmsAdapter {
  private val executorService = Executors.newCachedThreadPool()

  def withEnvironmentVariables(): KmsAdapter = {
    val keyArn = System.getenv("keyArn")
    val provider = new KmsMasterKeyProvider(new EnvironmentVariableCredentialsProvider(), keyArn)
    KmsAdapter(new AwsCrypto(), provider, executorService)
  }

  def withValues(keyArn: String, accessKey:String, secretKey:String): KmsAdapter = {
    val provider = new KmsMasterKeyProvider(new BasicAWSCredentials(accessKey, secretKey), keyArn)
    KmsAdapter(new AwsCrypto(), provider, executorService)
  }

  def prepareDataPackage(): DataPacket = DataPacket(List[Data]())
}

case class KmsAdapter(crypto: AwsCrypto, provider: KmsMasterKeyProvider, executorService: ExecutorService) {
  val timeInSeconds = TimeUnit.SECONDS.toSeconds(30)

  def crypt(dataPacket: DataPacket): DataPacketResult = {
    val listOfActions = dataPacket.values.map {
      data => EncryptAction(crypto, provider, data).asInstanceOf[Callable[Result]]
    }.asJava
    executeActions(listOfActions, Encrypt())
  }

  def decrypt(dataPacket: DataPacket): DataPacketResult = {
    val listOfActions = dataPacket.values.map {
      data => DecryptAction(crypto, provider, data).asInstanceOf[Callable[Result]]
    }.asJava
    executeActions(listOfActions, Decrypt())
  }

  private def executeActions(listOfActions: java.util.List[Callable[Result]], operation: Operation): DataPacketResult = {
    val listOfFutures = invokeAll(listOfActions)
    val listOfResults = processFutures(listOfFutures, operation)
    DataPacketResult(listOfResults)
  }

  private def processFutures(listOfFutures: List[Future[Result]], operation: Operation): List[Result] = {
    listOfFutures.map { future =>
      if (future.isCancelled) FailResult(operation)
      else future.get
    }
  }

  private def invokeAll(listOfActions: java.util.List[Callable[Result]]): List[Future[Result]] = {
    executorService.invokeAll(
      listOfActions,
      timeInSeconds,
      TimeUnit.SECONDS
    ).asScala.toList
  }
}
