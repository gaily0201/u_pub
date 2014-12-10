package cn.gaily.pub.ocr;

import java.io.File;
import java.io.IOException;

import cn.gaily.pub.screencapture.ScreenCapture;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

public class Test implements HotkeyListener{
	static final int KEY_1 = 88;  
    static final int KEY_2 = 89;  
    static final int KEY_3 = 90;
    
    static ScreenCapture capture = ScreenCapture.getInstance();
    File tempFile = new File("c:", "temp.png");
    OCR ocr=new OCR();
	
    
    public static void main(String[] args) throws Exception {
		Test test = new Test();
		test.initHotkey();
	}

		
		
	public void onHotKey(int key) {
		switch (key) {  
        case KEY_1:
        	System.out.println("ctrl+alt+I 按下.........");  
        	capture.captureImage();
    		try {
    			capture.saveAsPNG(tempFile);
				String maybe2 = ocr.recognizeText(new  File("c:\\temp.png"), "png");
				System.out.println(maybe2.trim());
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
            break;  
        case KEY_3:  
        	System.out.println("系统退出..........");  
        	capture.dispose();
            destroy();  
		}
	}
	
	/** 
     * 解除注册并退出 
     */  
	public void destroy() {  
        JIntellitype.getInstance().unregisterHotKey(KEY_1);  
        JIntellitype.getInstance().unregisterHotKey(KEY_3);  
        System.exit(0);  
    }
    
    public void initHotkey() {  
        JIntellitype.getInstance().registerHotKey(KEY_1, JIntellitype.MOD_CONTROL + JIntellitype.MOD_ALT, (int) 'I');  
        JIntellitype.getInstance().registerHotKey(KEY_3, JIntellitype.MOD_CONTROL + JIntellitype.MOD_ALT, (int) 'X');  
        JIntellitype.getInstance().addHotKeyListener(this);  
    }
    
    
}
