package br.codesimples.kms

import java.util.concurrent._

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.encryptionsdk.AwsCrypto
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider

import scala.collection.JavaConverters._

object KmsAdapter {
  private val executorService = Executors.newCachedThreadPool()
  private val keyArn = System.getenv("keyArn")
  private val provider = new KmsMasterKeyProvider(new EnvironmentVariableCredentialsProvider(), keyArn)
  private val kmsAdapter = KmsAdapter(new AwsCrypto(), provider, executorService)

  def withEnvironmentVariables(): KmsAdapter = kmsAdapter

  def prepareDataPackageWith(list:List[Data]): DataPacket = DataPacket(list)

  def prepareDataPackageWith(data: Data): DataPacket = DataPacket(List[Data](data))
}



case class KmsAdapter(crypto: AwsCrypto, provider: KmsMasterKeyProvider, executorService: ExecutorService) {
  val timeInSeconds = TimeUnit.SECONDS.toSeconds(30)

  def crypt(dataPacket: DataPacket): DataPacketResult = {
    val listOfActions = dataPacket.values.map {
      data => EncryptAction(crypto, provider, data).asInstanceOf[Callable[Result]]
    }.asJava
    executeActions(listOfActions)
  }

  def decrypt(dataPacket: DataPacket): DataPacketResult = {
    val listOfActions = dataPacket.values.map {
      data => DecryptAction(crypto, provider, data).asInstanceOf[Callable[Result]]
    }.asJava
    executeActions(listOfActions)
  }

  private def executeActions(listOfActions: java.util.List[Callable[Result]]): DataPacketResult = {
    val listOfFutures = invokeAll(listOfActions)
    val listOfResults = processFutures(listOfFutures)
    DataPacketResult(listOfResults)
  }

  private def processFutures(listOfFutures: List[Future[Result]]): List[Result] = {
    listOfFutures.map { future =>
      if (future.isDone) future.get()
      else FailResult
    }.asInstanceOf[List[Result]]
  }

  private def invokeAll(listOfActions: java.util.List[Callable[Result]]): List[Future[Result]] = {
    executorService.invokeAll(
      listOfActions,
      timeInSeconds,
      TimeUnit.SECONDS
    ).asScala.toList
  }
}
