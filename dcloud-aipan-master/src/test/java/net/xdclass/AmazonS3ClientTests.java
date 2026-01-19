package net.xdclass;

import cn.hutool.core.date.DateUtil;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
class AmazonS3ClientTests {
	
	@Autowired
	private AmazonS3Client amazonS3Client;
	
	
	//=========Bucket================

	/**
	 * 判断bucket是否存在
	 */
	@Test
	public void testBucketExists() {
		boolean bucketExist = amazonS3Client.doesBucketExist("ai-pan1");
		log.info("bucket是否存在:{}",bucketExist);
	}

	/**
	 * 创建bucket
	 */
	@Test
	public void testCreateBucket() {
		String bucketName = "ai-pan1";
		Bucket bucket = amazonS3Client.createBucket(bucketName);
		log.info("bucket:{}",bucket);
	}

	/**
	 * 删除bucket
	 */
	@Test
	public void testDeleteBucket() {
		String bucketName = "ai-pan1";
		amazonS3Client.deleteBucket(bucketName);
	}

	/**
	 * 获取全部bucket
	 */
	@Test
	public void testListBuckets() {
		for (Bucket bucket : amazonS3Client.listBuckets()) {
			log.info("bucket:{}",bucket.getName());
		}
	}

	/**
	 * 根据bucket名称获取bucket详情
	 */
	@Test
	public void testGetBucket() {
		String bucketName = "ai-pan1";
		Optional<Bucket> optionalBucket = amazonS3Client.listBuckets().stream().filter(bucket -> bucketName.equals(bucket.getName())).findFirst();
		if (optionalBucket.isPresent()) {
			log.info("bucket:{}",optionalBucket.get());
		}else {
			log.info("bucket不存在");
		}

	}


	//=====================操作文件相关===========================

	/**
	 * 上传单个文件，直接写入文本
	 */
	@Test
	public void testUploadFile() {
		PutObjectResult putObject = amazonS3Client.putObject("ai-pan", "test.txt", "hello world");
		log.info("putObject:{}",putObject);
	}

	/**
	 * 上传单个文件，采用本地文件路径
	 */
	@Test
	public void testUploadFileByPath() {
		PutObjectResult putObject = amazonS3Client.putObject("ai-pan", "/aa/bb/1111.png",
				new File("/Users/xdclass/Desktop/chunk/1.png"));
		log.info("putObject:{}",putObject);
	}


	/**
	 * 上传文件，输入流的方式，带上文件元数据
	 */
	@Test
	@SneakyThrows
	public void testUploadFileByInputStream() {
		try (FileInputStream inputStream = new FileInputStream("/Users/xdclass/Desktop/chunk/1.png")){
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentType("image/png");
			PutObjectResult putObject = amazonS3Client.putObject("ai-pan", "/aa/1.png", inputStream, objectMetadata);
			log.info("putObject:{}",putObject.getContentMd5());
		}
	}


	/**
	 * 获取文件
	 */
	@Test
	@SneakyThrows
	public void testGetFile() {
		try (FileOutputStream outputStream = new FileOutputStream(new File("/Users/xdclass/Desktop/chunk/test111.txt"))){
			S3Object s3Object = amazonS3Client.getObject("ai-pan", "test.txt");
			s3Object.getObjectContent().transferTo(outputStream);
		}

	}


	/**
	 * 删除文件
	 */
	@Test
	public void testDeleteFile() {
		amazonS3Client.deleteObject("ai-pan", "/aa/1.png");
	}


	/**
	 * 生成访问地址
	 */
	@Test
	public void testGeneratePresignedUrl() {
		// 预签名url过期时间(ms)
		long PRE_SIGN_URL_EXPIRE = 60 * 10 * 1000L;
		// 计算预签名url的过期日期
		Date expireDate = DateUtil.offsetMillisecond(new Date(), (int) PRE_SIGN_URL_EXPIRE);
		// 创建生成预签名url的请求，并设置过期时间和HTTP方法, withMethod是生成的URL访问方式,是权限控制的一种方式
		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest("ai-pan", "/aa/bb/1111.png")
				.withExpiration(expireDate).withMethod(HttpMethod.PUT);


		// 生成预签名url
		URL preSignedUrl = amazonS3Client.generatePresignedUrl(request);

		// 输出预签名url
		System.out.println(preSignedUrl.toString());
	}


	//=====================大文件上传相关接口===========================

	/**
	 * 第一步：初始化大文件分片上传任务，获取uploadId
	 * 如果初始化时有 uploadId，说明是断点续传，不能重新生成 uploadId
	 */
	@Test
	public void testInitiateMultipartUploadTask() {
		String bucketName = "ai-pan";
		String objectKey = "/aa/bb/cc/666.txt";

		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType("text/plain");

		//初始化分片上传请求
		InitiateMultipartUploadRequest initRequest =
				new InitiateMultipartUploadRequest(bucketName, objectKey, objectMetadata);

		//初始化分片上传任务
		InitiateMultipartUploadResult uploadResult = amazonS3Client.initiateMultipartUpload(initRequest);
		String uploadId = uploadResult.getUploadId();
		log.info("uploadId:{}",uploadId);

	}

	/**
	 * 第二步：测试初始化并生成多个预签名URL，返回给前端
	 */
	@Test
	public void testGenePreSignedUrls() {

		String bucketName = "ai-pan";
		String objectKey = "/aa/bb/cc/666.txt";
		//分片数量，这里配置4个
		int chunkCount = 4;
		String uploadId = "NzVhZjVjY2YtNzBhNS00YWE0LThjYjQtZmQzNmFkMTQyNTRmLmY1OGY2ZTEyLTY5YjYtNDQ3ZC04ZWMxLWJlZTVmOGFmZTkzYw";

		//存储预签名的地址
		List<String> preSignedUrls = new ArrayList<>(chunkCount);
		//遍历每个分片，生成预签名地址
		for (int i = 1; i <= chunkCount; i++) {
			//生成预签名URL,配置过期时间,1小时的时间
			Date expireDate = DateUtil.offsetMillisecond(new Date(), 3600 * 1000);
			//创建生成签名URL的请求，并且指定方法为PUT
			GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectKey)
					.withExpiration(expireDate).withMethod(HttpMethod.PUT);

			//添加上传ID，和分片编号做为请求参数
			request.addRequestParameter("uploadId", uploadId);
			request.addRequestParameter("partNumber", String.valueOf(i));
			//请求签名URL
			URL url = amazonS3Client.generatePresignedUrl(request);
			preSignedUrls.add(url.toString());
			log.info("preSignedUrl:{}",url);
		}


	}

	/**
	 * 合并分片
	 */
	@Test
	public void testMergeChunk() {

		String bucketName = "ai-pan";
		String objectKey = "/aa/bb/cc/666.txt";
		//分片数量，这里配置4个
		int chunkCount = 4;
		String uploadId = "NzVhZjVjY2YtNzBhNS00YWE0LThjYjQtZmQzNmFkMTQyNTRmLmY1OGY2ZTEyLTY5YjYtNDQ3ZC04ZWMxLWJlZTVmOGFmZTkzYw";

		//创建一个列出分片请求
		ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, objectKey, uploadId);
		PartListing partListing = amazonS3Client.listParts(listPartsRequest);
		List<PartSummary> partList = partListing.getParts();

		//检查分片数量和预期的是否一致
		if (partList.size() != chunkCount) {
			//已经上传的分片数量和记录中的不一样，不能合并
			throw new RuntimeException("分片数量不一致");
		}

		//创建完成分片上传请求对象，进行合并
		CompleteMultipartUploadRequest completeMultipartUploadRequest =
				new CompleteMultipartUploadRequest()
						.withBucketName(bucketName)
						.withKey(objectKey)
						.withUploadId(uploadId)
						.withPartETags(
								partList.stream()
										.map(partSummary ->
												new PartETag(partSummary.getPartNumber(), partSummary.getETag()))
										.collect(Collectors.toList()));

		//完成分片上传合并，获取结果
		CompleteMultipartUploadResult result = amazonS3Client.completeMultipartUpload(completeMultipartUploadRequest);
		log.info("result:{}",result.getLocation());


	}


	/**
	 * 其他步骤：上传进度验证，获取已经上传的分片文件, 未上传完成，调用接口获取上传进度
	 */
	@Test
	public void testGetUploadProgress() {
		String bucketName = "ai-pan";
		String objectKey = "/aa/bb/cc/666.txt";
		//分片数量，这里配置4个
		int chunkCount = 4;
		String uploadId = "NzVhZjVjY2YtNzBhNS00YWE0LThjYjQtZmQzNmFkMTQyNTRmLmY1OGY2ZTEyLTY5YjYtNDQ3ZC04ZWMxLWJlZTVmOGFmZTkzYw";

		//检查对应的桶里面是否存在对应的对象
		boolean doesObjectExist = amazonS3Client.doesObjectExist(bucketName, objectKey);
		if(!doesObjectExist){
			//未上传完成，返回已经上传的分片文件
			ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, objectKey, uploadId);
			PartListing partListing = amazonS3Client.listParts(listPartsRequest);
			List<PartSummary> partList = partListing.getParts();

			//创建一个结果，用于存储上传状态和分片列表
			Map<String,Object> result = new HashMap<>(2);
			result.put("finished",false);
			result.put("exitPartList",partList);

			//前端就可以通过这个判断是否要调用merge合并接口
			log.info("result:{}",result);

			//遍历每个分片的信息
			for (PartSummary partSummary : partList) {
				System.out.println("getPartNumber：" + partSummary.getPartNumber() +
						"，getETag=" + partSummary.getETag() + "，getSize= " + partSummary.getSize() +
						"，getLastModified=" + partSummary.getLastModified());
			}

		}


	}


}
