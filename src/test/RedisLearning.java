package test;

import java.io.File;
import java.util.HashSet;

import org.apache.log4j.Logger;

import image.Main;
import redis.clients.jedis.Jedis;

public class RedisLearning {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Logger logger = Logger.getLogger(Main.class);
		Jedis jedis = new Jedis("192.168.1.171");
		System.out.println("Connection to server sucessfully");
		// 查看服务是否运行
		File dirFile = new File("WanimalImg/");
//		dirFile.mkdirs();
		String[] imgsArray = dirFile.list();
		for (int i = 0; i < imgsArray.length; i++) {
			if (!jedis.exists(imgsArray[i])) {
				jedis.set(imgsArray[i], "");
				logger.info("-----new img : " + imgsArray[i] + " ------");
			}
			
		}
		logger.info("-----Redis size : " + jedis.keys("*").size() + " ------");
		
	}

}
