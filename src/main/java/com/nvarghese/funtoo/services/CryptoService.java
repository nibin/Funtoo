package com.nvarghese.funtoo.services;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nvarghese.funtoo.utils.ByteUtils;

public class CryptoService {
	
	static Logger logger = LoggerFactory.getLogger(CryptoService.class);

	public CryptoService() {

	}

	/**
	 * Create a temporary password by XOR-ing the input pass phrases
	 * 
	 * @param passPhrase1
	 * @param passPhrase2
	 * @return
	 */
	public byte[] createTempPassPhraseInBytes(String passPhrase1, String passPhrase2) {

		return ByteUtils.xorBytes(passPhrase1.getBytes(), passPhrase2.getBytes());
	}

	/**
	 * Generates symmetric key using AES algorithm
	 * 
	 * @param inputBytes
	 * @param randomSeedBytes
	 * @param keyLength
	 * @return
	 * @throws CryptoServiceException
	 */
	public byte[] generateKey(byte[] inputBytes, byte[] randomSeedBytes, int keyLength) throws CryptoServiceException {

		KeyGenerator generator = null;
		try {
			generator = KeyGenerator.getInstance("AES", "BC");
		} catch (NoSuchAlgorithmException e) {
			logger.error("Initialization of AES algorithm failed");
			CryptoServiceException cse = new CryptoServiceException("Initialization of AES algorithm failed");
			cse.initCause(e.getCause());
			throw cse;
		} catch (NoSuchProviderException e) {
			logger.error("BC provider cannot be found");
			CryptoServiceException cse = new CryptoServiceException("BC provider cannot be found");
			cse.initCause(e.getCause());
			throw cse;
		}
		SecureRandom r = new SecureRandom(ByteUtils.xorBytes(inputBytes, randomSeedBytes));

		generator.init(keyLength, r);
		Key encryptionKey = generator.generateKey();

		return encryptionKey.getEncoded();

	}

	/**
	 * Encrypts the input bytes using the transform "AES/CFB/NoPadding"
	 * 
	 * @param inputBytes
	 * @param keyBytes
	 * @param ivBytes
	 *            initialization vector bytes (Size should be 16 bytes)
	 * @return
	 * @throws InvalidKeyException
	 * @throws CryptoServiceException
	 */
	public byte[] symmetricEncrypt(byte[] inputBytes, byte[] keyBytes, byte[] ivBytes) throws InvalidKeyException,
			CryptoServiceException {

		byte[] cipherText = new byte[inputBytes.length];
		SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("AES/CFB/NoPadding", "BC");
			// cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
		} catch (NoSuchAlgorithmException e) {
			logger.error("Initialization of AES/CFB/NoPadding algorithm failed");
			CryptoServiceException cse = new CryptoServiceException(
					"Initialization of AES/CFB/NoPadding algorithm failed");
			cse.initCause(e.getCause());
			throw cse;
		} catch (NoSuchProviderException e) {
			logger.error("BC provider cannot be found");
			CryptoServiceException cse = new CryptoServiceException("BC provider cannot be found");
			cse.initCause(e.getCause());
			throw cse;
		} catch (NoSuchPaddingException e) {
			logger.error("Initialization of AES/CFB/NoPadding algorithm failed");
			CryptoServiceException cse = new CryptoServiceException(
					"Initialization of AES/CFB/NoPadding algorithm failed");
			cse.initCause(e.getCause());
			throw cse;
		}

		if (ivBytes == null) {
			throw new InvalidKeyException("ivBytes are null");
		} else {
			try {
				cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(ivBytes));
				// cipher.init(Cipher.ENCRYPT_MODE, key);
			} catch (InvalidAlgorithmParameterException e) {
				logger.error("Cipher init failed due to ivBytes");
				CryptoServiceException cse = new CryptoServiceException("Cipher init failed due to ivBytes");
				cse.initCause(e.getCause());
				throw cse;
			}
		}

		int ctLength = 0;

		try {
			ctLength = cipher.update(inputBytes, 0, inputBytes.length, cipherText, 0);
			//ctLength += cipher.doFinal(cipherText, ctLength);
			cipher.doFinal(cipherText, ctLength);
		} catch (ShortBufferException e) {
			logger.error("Cipher update failed due to buffer shortage");
			CryptoServiceException cse = new CryptoServiceException("Cipher update failed due to buffer shortage");
			cse.initCause(e.getCause());
			throw cse;
		} catch (IllegalBlockSizeException e) {
			logger.error("Cipher update failed due to illegal block size");
			CryptoServiceException cse = new CryptoServiceException("Cipher update failed due to illegal block size");
			cse.initCause(e.getCause());
			throw cse;
		} catch (BadPaddingException e) {
			logger.error("Cipher update failed due to bad padding");
			CryptoServiceException cse = new CryptoServiceException("Cipher update failed due to bad padding");
			cse.initCause(e.getCause());
			throw cse;
		}

		return cipherText;

	}

	/**
	 * Decrypts the cipher bytes using the transform "AES/CFB/NoPadding"
	 * 
	 * @param cipherBytes
	 * @param keyBytes
	 * @param ivBytes
	 *            initialization vector bytes (Size should be 16 bytes)
	 * @return
	 * @throws InvalidKeyException
	 * @throws CryptoServiceException
	 */
	public byte[] symmetricDecrypt(byte[] cipherBytes, byte[] keyBytes, byte[] ivBytes) throws InvalidKeyException,
			CryptoServiceException {

		SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
		Key decryptionKey = new SecretKeySpec(key.getEncoded(), key.getAlgorithm());
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("AES/CFB/NoPadding", "BC");
		} catch (NoSuchAlgorithmException e) {
			logger.error("Initialization of AES/CFB/NoPadding algorithm failed");
			CryptoServiceException cse = new CryptoServiceException(
					"Initialization of AES/CFB/NoPadding algorithm failed");
			cse.initCause(e.getCause());
			throw cse;
		} catch (NoSuchProviderException e) {
			logger.error("BC provider cannot be found");
			CryptoServiceException cse = new CryptoServiceException("BC provider cannot be found");
			cse.initCause(e.getCause());
			throw cse;
		} catch (NoSuchPaddingException e) {
			logger.error("Initialization of AES/CFB/NoPadding algorithm failed");
			CryptoServiceException cse = new CryptoServiceException(
					"Initialization of AES/CFB/NoPadding algorithm failed");
			cse.initCause(e.getCause());
			throw cse;
		}

		if (ivBytes == null) {
			throw new InvalidKeyException("ivBytes are null");
		} else {
			try {
				cipher.init(Cipher.DECRYPT_MODE, decryptionKey, new IvParameterSpec(ivBytes));
			} catch (InvalidAlgorithmParameterException e) {
				logger.error("Cipher init failed due to ivBytes");
				CryptoServiceException cse = new CryptoServiceException("Cipher init failed due to ivBytes");
				cse.initCause(e.getCause());
				throw cse;
			}
		}

		byte[] plainText = new byte[cipher.getOutputSize(cipherBytes.length)];
		int ptLength = 0;
		try {
			ptLength = cipher.update(cipherBytes, 0, cipherBytes.length, plainText, 0);
			ptLength += cipher.doFinal(plainText, ptLength);
		} catch (ShortBufferException e) {
			logger.error("Cipher update failed due to buffer shortage");
			CryptoServiceException cse = new CryptoServiceException("Cipher update failed due to buffer shortage");
			cse.initCause(e.getCause());
			throw cse;
		} catch (IllegalBlockSizeException e) {
			logger.error("Cipher update failed due to illegal block size");
			CryptoServiceException cse = new CryptoServiceException("Cipher update failed due to illegal block size");
			cse.initCause(e.getCause());
			throw cse;
		} catch (BadPaddingException e) {
			logger.error("Cipher update failed due to bad padding");
			CryptoServiceException cse = new CryptoServiceException("Cipher update failed due to bad padding");
			cse.initCause(e.getCause());
			throw cse;
		}

		return plainText;
	}

	/**
	 * Generates a keyPair using RSA algorithm
	 * 
	 * @param modulus
	 *            BigInteger value of base 16
	 * @param privateExponent
	 *            BigInteger value of base 16
	 * @param publicExponent
	 *            BigInteger value of base 16
	 * @return
	 * @throws CryptoServiceException
	 */
	public KeyPair generateKeyPair(BigInteger modulus, BigInteger privateExponent, BigInteger publicExponent)
			throws CryptoServiceException {

		KeyFactory keyFactory = null;
		try {
			keyFactory = KeyFactory.getInstance("RSA", "BC");
		} catch (NoSuchAlgorithmException e) {
			logger.error("Initialization of RSA algorithm failed");
			CryptoServiceException cse = new CryptoServiceException("Initialization of RSA algorithm failed");
			cse.initCause(e.getCause());
			throw cse;
		} catch (NoSuchProviderException e) {
			logger.error("BC provider cannot be found");
			CryptoServiceException cse = new CryptoServiceException("BC provider cannot be found");
			cse.initCause(e.getCause());
			throw cse;
		}

		RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(modulus, publicExponent);
		RSAPrivateKeySpec privKeySpec = new RSAPrivateKeySpec(modulus, privateExponent);

		RSAPublicKey pubKey = null;
		RSAPrivateKey privKey = null;

		try {
			pubKey = (RSAPublicKey) keyFactory.generatePublic(pubKeySpec);
			privKey = (RSAPrivateKey) keyFactory.generatePrivate(privKeySpec);
		} catch (InvalidKeySpecException e) {
			logger.error("Public/private key generation failed due to invalid keyspec");
			CryptoServiceException cse = new CryptoServiceException(
					"Public/private key generation failed due to invalid keyspec");
			cse.initCause(e.getCause());
			throw cse;
		}

		KeyPair keypair = new KeyPair(pubKey, privKey);
		return keypair;
	}

	/**
	 * Generates the key pair using RSA algorithm
	 * 
	 * @param randomSeedBytes1
	 * @param radomSeedBytes2
	 * @param keyLength
	 *            keylength in bits
	 * @return
	 * @throws CryptoServiceException
	 */
	public KeyPair generateKeyPair(byte[] randomSeedBytes1, byte[] radomSeedBytes2, int keyLength)
			throws CryptoServiceException {

		SecureRandom random = new SecureRandom(ByteUtils.xorBytes(randomSeedBytes1, radomSeedBytes2));

		KeyPairGenerator generator;
		try {
			generator = KeyPairGenerator.getInstance("RSA", "BC");
		} catch (NoSuchAlgorithmException e) {
			logger.error("Initialization of RSA algorithm failed");
			CryptoServiceException cse = new CryptoServiceException("Initialization of RSA algorithm failed");
			cse.initCause(e.getCause());
			throw cse;
		} catch (NoSuchProviderException e) {
			logger.error("BC provider cannot be found");
			CryptoServiceException cse = new CryptoServiceException("BC provider cannot be found");
			cse.initCause(e.getCause());
			throw cse;
		}

		generator.initialize(keyLength, random);

		KeyPair pair = generator.generateKeyPair();
		return pair;
	}

	/**
	 * Generates the key pair using RSA algorithm
	 * 
	 * @param random
	 *            SecureRandom object
	 * @param keyLength
	 *            key length in bits
	 * @return
	 * @throws CryptoServiceException
	 */
	public KeyPair generateKeyPair(SecureRandom random, Integer keyLength) throws CryptoServiceException {

		KeyPairGenerator generator;
		try {
			generator = KeyPairGenerator.getInstance("RSA", "BC");
		} catch (NoSuchAlgorithmException e) {
			logger.error("Initialization of RSA algorithm failed");
			CryptoServiceException cse = new CryptoServiceException("Initialization of RSA algorithm failed");
			cse.initCause(e.getCause());
			throw cse;
		} catch (NoSuchProviderException e) {
			logger.error("BC provider cannot be found");
			CryptoServiceException cse = new CryptoServiceException("BC provider cannot be found");
			cse.initCause(e.getCause());
			throw cse;
		}

		generator.initialize(keyLength.intValue(), random);

		KeyPair pair = generator.generateKeyPair();
		return pair;

	}

	/**
	 * Generates the RSA key pair based on key length
	 * @param keyLength
	 * @return
	 * @throws CryptoServiceException
	 */
	public KeyPair generateKeyPair(Integer keyLength) throws CryptoServiceException {

		KeyPairGenerator generator;
		try {
			generator = KeyPairGenerator.getInstance("RSA", "BC");
			generator.initialize(keyLength.intValue());
		} catch (NoSuchAlgorithmException e) {
			logger.error("Initialization of RSA algorithm failed");
			CryptoServiceException cse = new CryptoServiceException("Initialization of RSA algorithm failed");
			cse.initCause(e.getCause());
			throw cse;
		} catch (NoSuchProviderException e) {
			logger.error("BC provider cannot be found");
			CryptoServiceException cse = new CryptoServiceException("BC provider cannot be found");
			cse.initCause(e.getCause());
			throw cse;
		}

		KeyPair pair = generator.generateKeyPair();
		return pair;

	}

	/**
	 * Encrypts the input bytes using "RSA/None/NoPadding". Library will encrypt
	 * a single block size of 127 bytes
	 * 
	 * @param inputBytes
	 *            inputBytes of size 127 bytes
	 * @param keyPair
	 * @param random
	 * @return
	 * @throws CryptoServiceException
	 * @throws Exception
	 */
	public byte[] asymmetricEncrypt(byte[] inputBytes, KeyPair keyPair) throws CryptoServiceException {

		Cipher cipher;
		try {
			cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
		} catch (NoSuchAlgorithmException e) {
			logger.error("Initialization of RSA/None/NoPadding algorithm failed");
			CryptoServiceException cse = new CryptoServiceException(
					"Initialization of RSA/None/NoPadding algorithm failed");
			cse.initCause(e.getCause());
			throw cse;
		} catch (NoSuchProviderException e) {
			logger.error("BC provider cannot be found");
			CryptoServiceException cse = new CryptoServiceException("BC provider cannot be found");
			cse.initCause(e.getCause());
			throw cse;
		} catch (NoSuchPaddingException e) {
			logger.error("Initialization of RSA/None/NoPadding algorithm failed");
			CryptoServiceException cse = new CryptoServiceException(
					"Initialization of RSA/None/NoPadding algorithm failed");
			cse.initCause(e.getCause());
			throw cse;
		}

		try {
			cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
		} catch (InvalidKeyException e) {
			logger.error("Cipher init failed due to invalid public key");
			CryptoServiceException cse = new CryptoServiceException("Cipher init failed due to invalid public key");
			cse.initCause(e.getCause());
			throw cse;
		}

		byte[] cipherBytes = null;

		try {
			cipherBytes = cipher.doFinal(inputBytes);
		} catch (IllegalBlockSizeException e) {
			logger.error("Cipher update failed due to illegal block size");
			CryptoServiceException cse = new CryptoServiceException("Cipher update failed due to illegal block size");
			cse.initCause(e.getCause());
			throw cse;
		} catch (BadPaddingException e) {
			logger.error("Cipher update failed due to bad padding");
			CryptoServiceException cse = new CryptoServiceException("Cipher update failed due to bad padding");
			cse.initCause(e.getCause());
			throw cse;
		}

		return cipherBytes;
	}

	/**
	 * Decrypts the cipher bytes using "RSA/None/NoPadding"
	 * 
	 * @param cipherBytes
	 * @param keyPair
	 * @param random
	 * @return
	 * @throws CryptoServiceException
	 */
	public byte[] asymmetricDecrypt(byte[] cipherBytes, KeyPair keyPair) throws CryptoServiceException {

		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
		} catch (NoSuchAlgorithmException e) {
			logger.error("Initialization of RSA/None/NoPadding algorithm failed");
			CryptoServiceException cse = new CryptoServiceException(
					"Initialization of RSA/None/NoPadding algorithm failed");
			cse.initCause(e.getCause());
			throw cse;
		} catch (NoSuchProviderException e) {
			logger.error("BC provider cannot be found");
			CryptoServiceException cse = new CryptoServiceException("BC provider cannot be found");
			cse.initCause(e.getCause());
			throw cse;
		} catch (NoSuchPaddingException e) {
			logger.error("Initialization of RSA/None/NoPadding algorithm failed");
			CryptoServiceException cse = new CryptoServiceException(
					"Initialization of RSA/None/NoPadding algorithm failed");
			cse.initCause(e.getCause());
			throw cse;
		}

		try {
			cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
		} catch (InvalidKeyException e) {
			logger.error("Cipher init failed due to invalid public key");
			CryptoServiceException cse = new CryptoServiceException("Cipher init failed due to invalid public key");
			cse.initCause(e.getCause());
			throw cse;
		}

		byte[] plainText = null;

		try {
			plainText = cipher.doFinal(cipherBytes);
		} catch (IllegalBlockSizeException e) {
			logger.error("Cipher update failed due to illegal block size");
			CryptoServiceException cse = new CryptoServiceException("Cipher update failed due to illegal block size");
			cse.initCause(e.getCause());
			throw cse;
		} catch (BadPaddingException e) {
			logger.error("Cipher update failed due to bad padding");
			CryptoServiceException cse = new CryptoServiceException("Cipher update failed due to bad padding");
			cse.initCause(e.getCause());
			throw cse;
		}

		return plainText;

	}

	public BigInteger getModulus(KeyPair keyPair) throws CryptoServiceException {

		KeyFactory fact;
		BigInteger modulus = null;
		try {
			fact = KeyFactory.getInstance("RSA", "BC");
			RSAPublicKeySpec pub = fact.getKeySpec(keyPair.getPublic(), RSAPublicKeySpec.class);			
			modulus = pub.getModulus();

		} catch (NoSuchAlgorithmException e) {
			logger.error("Initialization of RSA/None/NoPadding algorithm failed");
			CryptoServiceException cse = new CryptoServiceException(
					"Initialization of RSA/None/NoPadding algorithm failed");
			cse.initCause(e.getCause());
			throw cse;
		} catch (NoSuchProviderException e) {
			logger.error("BC provider cannot be found");
			CryptoServiceException cse = new CryptoServiceException("BC provider cannot be found");
			cse.initCause(e.getCause());
			throw cse;
		} catch (InvalidKeySpecException e) {
			logger.error("Cipher init failed due to invalid keyspec");
			CryptoServiceException cse = new CryptoServiceException("Cipher init failed due to invalid keyspec");
			cse.initCause(e.getCause());
			throw cse;
		}

		return modulus;

	}

}
