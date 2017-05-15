package br.codesimples.kms

import java.util.concurrent._

import com.amazonaws.auth.{BasicAWSCredentials, EnvironmentVariableCredentialsProvider}
import com.amazonaws.encryptionsdk.AwsCrypto
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider

import scala.collection.JavaConverters._

object KmsAdapterFromEnvironmentVariables {
  private val executorService = Executors.newCachedThreadPool()
  private val keyArn = System.getenv("keyArn")
  private val provider = new KmsMasterKeyProvider(new EnvironmentVariableCredentialsProvider(), keyArn)

  def kmsAdapter(): KmsAdapter = {
    KmsAdapter(new AwsCrypto(), provider, executorService)
  }
}

object KmsAdapterFromVariables {
  private val executorService = Executors.newCachedThreadPool()

  def buildProvider(keyArn: String, accessKey:String, secretKey:String): KmsMasterKeyProvider = {
    new KmsMasterKeyProvider(new BasicAWSCredentials(accessKey, secretKey), keyArn)
  }

  def kmsAdapter(provider: KmsMasterKeyProvider): KmsAdapter = {
    KmsAdapter(new AwsCrypto(), provider, executorService)
  }
}

object KmsAdapter {
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
