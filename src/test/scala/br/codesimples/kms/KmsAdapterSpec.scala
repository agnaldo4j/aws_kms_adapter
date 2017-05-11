package br.codesimples.kms

import org.specs2.mutable.Specification

class KmsAdapterSpec extends Specification {

  isolated
  sequential
  "The kms adapter showd" should {
      args(sequential=true, isolated=true)
      "encrypt data" ! workerForTest().encryptData()
    }

    case class workerForTest() {

      def encryptData() = {
        success
      }
    }
  }

