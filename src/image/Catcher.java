package image;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class Catcher {

	static Logger logger = Logger.getLogger(Main.class);
	public static String HOME_WANIMAL = "http://wanimal1983.org/";
	private LinkedList<String> loadingQueue = new LinkedList<String>();
	public static String PARENT_PATH = "WanimalImg/";
//	private ConcurrentLinkedQueue<String> reloadingQueue = new ConcurrentLinkedQueue<String>();
	private HashSet<String> imgNameSet;
	private int imgUrlCount;
	private int connectTimeout = 5000;
	private int readTimeout = 10000;

	// ��ҳץȡ+�����ж�
	public void catchingAllPage() {
		// ���ļ��������е��ļ�����������
		File dirFile = new File(PARENT_PATH);
		dirFile.mkdirs();
		String[] imgsArray = dirFile.list();
		imgNameSet = new HashSet<>();
		for (int i = 0; i < imgsArray.length; i++) {
			imgNameSet.add(imgsArray[i]);
		}

		boolean isPaging = findImgURLinPage(HOME_WANIMAL);
		loadingImg();
		String prefix = HOME_WANIMAL + "page/";
		int i = 2;
		while (isPaging && findImgURLinPage(prefix + i)) {
			loadingImg();
			i++;
		}
		loadingImg();
		logger.info("---THE END: All the pages have been load---");

	}

	// ץȡһҳ������ͼƬ��URL������imgURLlist
	private boolean findImgURLinPage(String urlPara) {
		int countTmp = 0; //�Ѿ�ץȡ����img URL�ĸ�����ҳ���ȡ��ʱ���ٴ����·���ʱ���������˱�־������һ��ҳ��Ķ�ȡλ��
		try {
			URL url = new URL(urlPara);
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			InputStreamReader data = new InputStreamReader(
					connection.getInputStream(), "UTF-8");
			BufferedReader dataBuffered = new BufferedReader(data);
			String lineInPage;
			logger.info(urlPara + " begin");
			
			while ((lineInPage = dataBuffered.readLine()) != null) {
				Pattern imgPattern = Pattern.compile("<img src=\"(.*?jpg)\"");
				Matcher matcher = imgPattern.matcher(lineInPage);
				if (matcher.find()) {
					String imgURL = matcher.group(1);
					countTmp++;
					if (countTmp <= imgUrlCount) continue;
					if (imgNameSet.contains(imgURL.split("/")[3] + ".jpg")) {
						logger.info("---- All the lastest URL of image have done ----");
						return false;
					}

					logger.info(imgURL);
					loadingQueue.offer(imgURL);

				}
			}
			if (countTmp == 0) return false;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.warn("", e);
			return false;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.error("", e);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("", e);
			imgUrlCount = countTmp;
			logger.info("Request page again: " + urlPara);
			return findImgURLinPage(urlPara);
		}
		imgUrlCount = 0;
		return true;
	}

	// ���̲߳�������ͼƬ�ֽ���
	private void loadingImg() {
		while (!loadingQueue.isEmpty()) {
			final String url = loadingQueue.poll();
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					loadAndWriteImg(url);

				}
			});
			thread.start();
		}
	}

	// ���߳���һ����ͼƬ�ֽ�������ʱ�����ر�ĳһ��ͼƬ����ʱ��ܳ���ʱ��
	public void loadingImgSingleThread() {
		long begin = System.currentTimeMillis();
		while (!loadingQueue.isEmpty()) {
			String url = loadingQueue.poll();
			loadAndWriteImg(url);
		}
		System.out.println("All catching picture time: "
				+ (System.currentTimeMillis() - begin));
	}

	/**
	 * ����URL����ͼƬ�ֽ���Ȼ���û���IOд���ļ�
	 * 
	 * @param imgUrlPara
	 *            : http://41.media.tumblr.com/84ad29fbb73ebd861d84075f5104ca99/
	 *            tumblr_npu1ij2uFy1r2xjmjo1_1280.jpg
	 */
	private void loadAndWriteImg(String imgUrlPara) {
		String filePath = PARENT_PATH + imgUrlPara.split("/")[3] + ".jpg";
		File imgFile = new File(filePath);
		if (!imgFile.getParentFile().exists())
			imgFile.getParentFile().mkdirs();
		try {
			logger.debug(filePath + " start loading");
			URL imgUrl = new URL(imgUrlPara);
			URLConnection connection = imgUrl.openConnection();
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			InputStream imgIn = connection.getInputStream();
			logger.debug(filePath + " finish loading");
			FileOutputStream imgOut = new FileOutputStream(filePath);
			BufferedOutputStream bufImgOut = new BufferedOutputStream(imgOut);
			long timeNow = System.currentTimeMillis();
			int ch = 0;
			while ((ch = imgIn.read()) != -1) {
				bufImgOut.write(ch);
			}
			logger.info(filePath + " written Time: "
					+ (System.currentTimeMillis() - timeNow));
			bufImgOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
//			reloadingQueue.offer(imgUrlPara);
			logger.info("To be reloading: " + imgUrlPara);
			loadAndWriteImg(imgUrlPara);
		}

	}
	
	public static void main(String[] args) {
		Catcher catcher = new Catcher();
		catcher.loadingImg();
	}

}
