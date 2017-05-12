package br.codesimples.kms

import org.specs2.mutable.Specification

class KmsAdapterSpec extends Specification {

  isolated
  sequential
  "The kms adapter showd" should {
    args(sequential=true, isolated=true)
    "encrypt data" ! workerForTest().cryptData()
  }

  private def toCryptDataPacket(): DataPacket = {
    DataPacket(
      List[Value](
        Value("name", "Agnaldo de Oliveira")
      )
    )
  }

  private def toDecryptDataPacket(): DataPacket = {
    DataPacket(
      List[Value](
        Value(
          "name",
          "AYADeDklv2yjDEIlnEyUviGSYYIAXwABABVhd3MtY3J5cHRvLXB1YmxpYy1rZXkAREF0OUg1TXdHKzB3azl0VTk0cDIyNVRTRVdNUFV5a0N4anFzSDMraTZFaytCMjNaOXE2RVBhUnYxcjNHS0JHRkpnUT09AAEAB2F3cy1rbXMAS2Fybjphd3M6a21zOnVzLWVhc3QtMTo2MDU0NzYyNDIyNzQ6a2V5L2FjMTUxNDFiLTYwOWUtNDg4My04ZWJmLTg3YWQ4ZDA3OGUyZgCnAQEBAHg1v2v0v")
      )
    )
  }

  case class workerForTest() {
    def cryptData() = {
      val adapter = KmsAdapter.newWithEnvironmentVariables()
      val result = adapter.crypt( toCryptDataPacket() )
      val listOfValues = result.values
      listOfValues.size must be equalTo(1)
      listOfValues.head.attribute must be equalTo("name")
      listOfValues.head.value.length must be greaterThan(20)
    }

    def decryptData() = {
      val adapter = KmsAdapter.newWithEnvironmentVariables()
      val result = adapter.decrypt( toDecryptDataPacket() )
      val listOfValues = result.values
      listOfValues.size must be equalTo(1)
      listOfValues.head.attribute must be equalTo("name")
      listOfValues.head.value must be equalTo("Agnaldo de Oliveira")
    }
  }
}