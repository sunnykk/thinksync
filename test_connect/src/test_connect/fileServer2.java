package test_connect;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;


public class fileServer2 {

	public static void main(String[] args){
		
		int PORT = 5501;
		
		ServerSocket serverSocket = null;
		Socket socket = null;
		
		try{
			serverSocket = new ServerSocket(PORT);
			
			while(true){
				System.out.println("연결대기중----");
				
				socket = serverSocket.accept();
				
				Receiver receiver = new Receiver(socket);
				receiver.start();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}

class Receiver extends Thread{
	
	Socket socket;
	DataInputStream dis = null;
	FileOutputStream fos = null;
	BufferedOutputStream bos = null;
	
	public Receiver(Socket socket){
		this.socket=socket;
	}
	
	public void run(){
		String result=null;
		try{
			dis = new DataInputStream(socket.getInputStream());
			String type = dis.readUTF();
			
			if(type.equals("file")){
				result = fileWrite(dis);
				System.out.println("결과 "+result);
			}
			else if(type.equals("msg")){
				result = textReader(socket);
				System.out.println("결과 "+result);
			}
			else if(type.equals("image")){
				result = imageReader(socket);
				System.out.println("결과 "+result);
			}
		}catch(IOException e){
			System.out.println("run() Failed!");
			e.printStackTrace();
		}
	}
	
	private String fileWrite(DataInputStream dis){
		String result=null;;
		String filePath="C:/Users/sun/Desktop/new_work/test_connect/src/test_connect"; //파일경로지정
		
		try{
			String fileName = dis.readUTF();
			System.out.println("파일명 :"+fileName+"받음");
			
			//**실체 파일 생성된당 (??사진은 어케할거니)
			File file = new File(filePath+"/"+fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			System.out.println(fileName+" 파일생성");
			
			int len;
			int size = 4096;
			byte[] data = new byte[size];
			while((len=dis.read(data))!= -1){
				bos.write(data,0,len);
			}
			//bos.flush();
			result = "SUCCESS";
			
			System.out.println("파일수신 완료. 사이즈:"+file.length());
		}catch(IOException e){
			e.printStackTrace();
		}finally{
	    	try{bos.close();}catch(IOException e){e.printStackTrace();}
	    	try{fos.close();}catch(IOException e){e.printStackTrace();}
	    	try{dis.close();}catch(IOException e){e.printStackTrace();}
	    }
		return result;
	}
	
	private String textReader(Socket soket){
		String result=null;
		String msg=null;
		
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		
		try{
			
			is = socket.getInputStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			// 클라이언트로부터 데이터를 받기 위한 InputStream 선언

			msg = br.readLine();
			System.out.println("클라이언트로 부터 받은 데이터:" + msg);
			
			result = "SUCCESS";
			
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("textReader error");
		}finally{
	    	try{
	    		//dis.close();
	    		br.close();
				isr.close();
				is.close();
	    		}catch(IOException e){
	    			e.printStackTrace();
	    		}
	    }
		return result;
	}
	
	private String imageReader(Socket soket){
		String result=null;

		//이미지를 읽기고 저장하기 위한 버퍼, 스트림
		InputStream is = null;
		BufferedImage bimg = null;
		FileOutputStream fout=null;
		
		try{
			is = socket.getInputStream();
			
			bimg = ImageIO.read(is);
			fout = new FileOutputStream("upload.jpg"); //**이미지받는 디렉토리 설정
			ImageIO.write(bimg, "jpg", fout);

			fout.close();

			System.out.println("서버 :이미지 수신 및 파일에 저장 완료");
			
			result = "SUCCESS";
			
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("textReader error");
		}finally{
	    	try{
				is.close();
	    		}catch(IOException e){
	    			e.printStackTrace();
	    		}
	    }
		return result;
	}
	
}//Reciever 끝