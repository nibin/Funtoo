package com.nvarghese.funtoo.services;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.nvarghese.funtoo.services.CryptoService;
import com.nvarghese.funtoo.services.CryptoServiceException;
import com.nvarghese.funtoo.utils.ByteUtils;


public class TestCryptoService {

	CryptoService cryptoService;
	final byte[] inputBytes = "This is a simple text".getBytes();

	final byte[] ivBytes = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x02, 0x03, 0x00,
			0x00, 0x00, 0x01 };

	byte[] keyBytes;
	byte[] cipherBytes;

	KeyPair keyPair;
	byte[] cipherAsymBytes;

	@BeforeClass
	public void initialize() {

		cryptoService = new CryptoService();

	}

	@Test
	public void testCreateTempPassPhraseWithEqualParams() {

		byte[] tempPassPhrasebytes = cryptoService.createTempPassPhraseInBytes("Password1", "Password2");
		Assert.assertNotNull(tempPassPhrasebytes);
		Assert.assertEquals(tempPassPhrasebytes.length, 9);
		// 0x49^0x50 = 0x03
		Assert.assertEquals(tempPassPhrasebytes[8], 0x03);
	}

	@Test
	public void testCreateTempPassPhraseWithUnequalParams() {

		byte[] tempPassPhrasebytes = cryptoService.createTempPassPhraseInBytes("Password123", "Password2");
		Assert.assertNotNull(tempPassPhrasebytes);
		Assert.assertEquals(tempPassPhrasebytes.length, "Password123".length());
		// 0x49^0x50 = 0x03
		Assert.assertEquals(tempPassPhrasebytes[8], 0x03);
		Assert.assertEquals(tempPassPhrasebytes[9], "2".getBytes()[0]);
	}

	@Test
	public void testGenerateKey() throws Exception {

		SecureRandom random = new SecureRandom();
		keyBytes = cryptoService.generateKey(cryptoService.createTempPassPhraseInBytes("Password1", "Password2"),
				random.generateSeed(32), 256);
		Assert.assertNotNull(keyBytes);
		Assert.assertEquals(keyBytes.length, 32);

	}

	@Test(dependsOnMethods = { "testGenerateKey" })
	public void testEncryptWithNoPaddingCipher() throws Exception {

		cipherBytes = cryptoService.symmetricEncrypt(inputBytes, keyBytes, ivBytes);

		Assert.assertNotNull(cipherBytes);
		Assert.assertEquals(inputBytes.length, cipherBytes.length);

	}

	@Test(dependsOnMethods = { "testEncryptWithNoPaddingCipher" })
	public void testDecryptWithNoPaddingCipher() throws Exception {

		byte[] decryptedBytes = cryptoService.symmetricDecrypt(cipherBytes, keyBytes, ivBytes);

		Assert.assertNotNull(decryptedBytes);
		Assert.assertEquals(decryptedBytes, inputBytes);

	}

	@Test
	public void testGenerateKeyPairWithModulusAndExponentParams() throws CryptoServiceException {

		BigInteger modulus = new BigInteger("11", 16);
		BigInteger privateExponent = new BigInteger("d46f473a2d746537de2056ae3092c451", 16);
		BigInteger publicExponent = new BigInteger("57791d5430d593164082036ad8b29fb1", 16);

		KeyPair keyPair = cryptoService.generateKeyPair(modulus, privateExponent, publicExponent);

		Assert.assertNotNull(keyPair);
		Assert.assertNotNull(keyPair.getPrivate());
		Assert.assertNotNull(keyPair.getPublic());

	}

	@Test
	public void testGenerateKeyPairWithRandomSeedParams() throws CryptoServiceException {

		keyPair = cryptoService.generateKeyPair(ByteUtils.getRandomBytes(256), ByteUtils.getRandomBytes(256), 1024);

		Assert.assertNotNull(keyPair);
		Assert.assertNotNull(keyPair.getPrivate());
		Assert.assertNotNull(keyPair.getPublic());
	}

	@Test(dataProvider = "feedKeyLength")
	public void testRegenerateOfKeyPair(int len) throws InvalidKeySpecException, NoSuchAlgorithmException,
			NoSuchProviderException, CryptoServiceException {

		byte[] inBytes = ByteUtils.getRandomBytes(len);

		KeyPair originalKp = cryptoService.generateKeyPair(1024);

		KeyFactory fact = KeyFactory.getInstance("RSA", "BC");
		RSAPublicKeySpec pub = fact.getKeySpec(originalKp.getPublic(), RSAPublicKeySpec.class);
		RSAPrivateKeySpec priv = fact.getKeySpec(originalKp.getPrivate(), RSAPrivateKeySpec.class);

		BigInteger modulus = pub.getModulus();
		BigInteger privateExponent = priv.getPrivateExponent();
		BigInteger publicExponent = pub.getPublicExponent();
		
//		System.out.println("Big str: " + modulus.toString());
//		System.out.println("Big hex: " + ByteUtils.toHex(modulus.toByteArray()));
//		System.out.println("Big lng: " + modulus.longValue());
		

		KeyPair regeneratedKeyPair = cryptoService.generateKeyPair(modulus, privateExponent, publicExponent);

		BigInteger m = modulus;
		BigInteger e = publicExponent;
		RSAPublicKeySpec rsakeySpec = new RSAPublicKeySpec(m, e);
		PublicKey pubKey = fact.generatePublic(rsakeySpec);
		RSAPrivateKeySpec rsaprivSpec = new RSAPrivateKeySpec(m, privateExponent);
		PrivateKey privKey = fact.generatePrivate(rsaprivSpec);

//		System.out.println("Original PrivKey: " + ByteUtils.toHex(originalKp.getPrivate().getEncoded()));
//		System.out.println("Generatd PrivKey: " + ByteUtils.toHex(regeneratedKeyPair.getPrivate().getEncoded()));
//		System.out.println("Specgenn PrivKey: " + ByteUtils.toHex(privKey.getEncoded()));
//		System.out.println("Original  PubKey: " + ByteUtils.toHex(originalKp.getPublic().getEncoded()));
//		System.out.println("Generatd  PubKey: " + ByteUtils.toHex(regeneratedKeyPair.getPublic().getEncoded()));

		Assert.assertEquals(regeneratedKeyPair.getPublic().getEncoded(), originalKp.getPublic().getEncoded());
		// Assert.assertEquals(regeneratedKeyPair.getPrivate().getEncoded(),
		// originalKp.getPrivate().getEncoded());

		byte[] cbWithOriginalKeyPair = cryptoService.asymmetricEncrypt(inBytes, originalKp);
		byte[] cbWithRegeneratedKeyPair = cryptoService.asymmetricEncrypt(inBytes, regeneratedKeyPair);

		byte[] pbWithOriginalKeyPair = cryptoService.asymmetricDecrypt(cbWithOriginalKeyPair, originalKp);
		byte[] pbWithRegeneratedKeyPair = cryptoService.asymmetricDecrypt(cbWithRegeneratedKeyPair, regeneratedKeyPair);

		byte[] pbWithRegeneratedKeyPairEncryptedByOriginalKeyPair = cryptoService.asymmetricDecrypt(
				cbWithOriginalKeyPair, regeneratedKeyPair);

		Assert.assertEquals(pbWithOriginalKeyPair, inBytes);
		Assert.assertEquals(pbWithRegeneratedKeyPairEncryptedByOriginalKeyPair, inBytes);
		Assert.assertEquals(pbWithRegeneratedKeyPair, inBytes);

	}

	@DataProvider
	public Object[][] feedKeyLength() {

		return new Object[][] { { 127 } };
	}

	@Test(dependsOnMethods = { "testGenerateKeyPairWithRandomSeedParams" })
	public void testAsymmetricEncryption() throws CryptoServiceException {

		cipherAsymBytes = cryptoService.asymmetricEncrypt(inputBytes, keyPair);

		Assert.assertNotNull(cipherAsymBytes);
		Assert.assertNotSame(cipherAsymBytes, inputBytes);

	}

	@Test(dependsOnMethods = { "testAsymmetricEncryption" })
	public void testAsymmetricDecryption() throws CryptoServiceException {

		byte[] pBytes = cryptoService.asymmetricDecrypt(cipherAsymBytes, keyPair);

		Assert.assertNotNull(pBytes);
		Assert.assertEquals(pBytes, inputBytes);

	}

	@Test(dependsOnMethods = { "testGenerateKeyPairWithRandomSeedParams" })
	public void testAsymmetricEncryptionAndDecryptionWithLargerDataSize() throws CryptoServiceException,
			InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {

		byte[] inBytes = ByteUtils.getRandomBytes(512);
		KeyFactory fact = KeyFactory.getInstance("RSA", "BC");
		RSAPublicKeySpec pub = fact.getKeySpec(keyPair.getPublic(), RSAPublicKeySpec.class);
		RSAPrivateKeySpec priv = fact.getKeySpec(keyPair.getPrivate(), RSAPrivateKeySpec.class);

		BigInteger modulus = pub.getModulus();
		BigInteger privateExponent = priv.getPrivateExponent();
		BigInteger publicExponent = pub.getPublicExponent();

		//System.out.println("Big str: " + modulus.toString());
		//System.out.println("Big hex: " + ByteUtils.toHex(modulus.toByteArray()));
		//System.out.println("Big lng: " + modulus.longValue());
		KeyPair regeneratedKeyPair = cryptoService.generateKeyPair(modulus, privateExponent, publicExponent);

		ByteArrayOutputStream cout = new ByteArrayOutputStream(inBytes.length);
		ByteArrayOutputStream pOut = new ByteArrayOutputStream(inBytes.length);

		int offset = 0;
		while (offset <= inBytes.length) {
			byte[] copy = Arrays.copyOfRange(inBytes, offset, offset + 127);
			byte[] bytes = cryptoService.asymmetricEncrypt(copy, regeneratedKeyPair);
			cout.write(bytes, 0, bytes.length);

			offset += 127;

		}

		// System.out.println("Cipher byte length is :" +
		// cout.toByteArray().length);
		byte[] cBytes = cout.toByteArray();
		offset = 0;
		while (offset <= cBytes.length) {
			// ByteArrayOutputStream out = new ByteArrayOutputStream(128);
			// out.write(inBytes, offset, 128);
			byte[] iopy = Arrays.copyOfRange(cBytes, offset, offset + 128);
			byte[] bytes = cryptoService.asymmetricDecrypt(iopy, regeneratedKeyPair);
			pOut.write(bytes, 0, bytes.length);

			offset += 128;

		}
//		 System.out.println("Plain byte length is :" +
//		 pOut.toByteArray().length);
//		 System.out.println("Original:: " + ByteUtils.toHex(inBytes));
//		 System.out.println("After  D:: " +
//		 ByteUtils.toHex(pOut.toByteArray()));
//		 System.out.println("After  E:: " + ByteUtils.toHex(cBytes));
		//System.out.println("Length of private: " + regeneratedKeyPair.getPrivate().getEncoded().length);
		//System.out.println("Length of public: " + regeneratedKeyPair.getPublic().getEncoded().length);

		Assert.assertEquals(inBytes, Arrays.copyOfRange(pOut.toByteArray(), 0, inBytes.length));

	}

	@AfterClass
	public void cleanup() {

	}
}