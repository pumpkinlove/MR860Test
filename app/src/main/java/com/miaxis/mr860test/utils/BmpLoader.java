package com.miaxis.mr860test.utils;

public class BmpLoader {
	
	 /**
     * Convert an int to a byte array
     * 
     * @param value
     *            int
     * @return byte[]
     */
    public static byte[] intToByteArray(int value) {
            byte[] b = new byte[4];
            for (int i = 0; i < 4; i++) {
                    int offset = (i) * 8;
                    b[i] = (byte) ((value >> offset) & 0xFF);
            }
            return b;
    }

    /**
     * Convert the byte array to an int starting from the given offset.
     * 
     * @param b
     *            The byte array
     * @param offset
     *            The array offset,婵″倹鐏塨yte閺佹壆绮嶉梹鍨鐏忚鲸妲�閿涘苯鍨拠銉ワ拷娑擄拷
     * @return The integer
     */

    public static int byteArrayToInt(byte[] b, int offset) {
            int value = 0;
            for (int i = 0; i < 4; i++) {
                    int shift = (i) * 8;
                    value += (b[i + offset] & 0x000000FF) << shift;
            }
            return value;
    }
    
	public static int Bmp2Raw(byte[] pBmp,byte[] pRaw,byte[] pW,byte[] pH)
	{ 
	 int i,X,Y;
	 byte[] head = new byte[1078];
	 System.arraycopy(pBmp, 0, head, 0, 26);
	 X=head[18]+(head[19]<<8)+(head[20]<<8)+(head[21]<<8);
	 Y=head[22]+(head[23]<<8)+(head[24]<<8)+(head[25]<<8);
	
	 if(X<0)
	 {
		 X= X+256;
	 }
	 if(Y<0)
	 {
		 Y= Y+256;
	 }
	 System.arraycopy(intToByteArray(X),0, pW,0, 4);
	 System.arraycopy(intToByteArray(Y),0, pH,0, 4);
	 for( i=0;i<Y; i++ )
	 {
		System.arraycopy(pBmp,1078+(Y-1-i)*X, pRaw,i*X, X);
	 }
	 return 0;
	}
	
	public static int Raw2Bmp(byte[] pBmp,byte[] pRaw,int X,int Y)
	{
	 int num;
	 int i,j = 0;
	 byte[] head= new byte[1078];
	 
	 byte[] temp ={
			 0x42,0x4d,			//file header
			 0x0,0x00,0x0,0x00, 	//file size***
			 0x00,0x00, //reserved
			 0x00,0x00,//reserved
			 0x36,0x4,0x00,0x00,//head byte***
			 0x28,0x00,0x00,0x00,//struct size
			 0x00,0x00,0x00,0x00,//map width*** 
			 0x00,0x00,0x00,0x00,//map height***
			 0x01,0x00,//must be 1
			 0x08,0x00,//color count***
			 0x00,0x00,0x00,0x00, //compression
			 0x00,0x00,0x00,0x00,//data size***
			 0x00,0x00,0x00,0x00, //dpix
			 0x00,0x00,0x00,0x00, //dpiy
			 0x00,0x00,0x00,0x00,//color used
			 0x00,0x00,0x00,0x00,//color important   
	 };
	 
	 System.arraycopy(temp, 0, head, 0, temp.length);
	 
	 //绾喖鐣鹃崶鎹愯杽鐎硅棄瀹抽弫鏉匡拷
	 num=X; head[18]= (byte) (num & 0xFF);
	 num=num>>8;  head[19]= (byte) (num & 0xFF);
	 num=num>>8;  head[20]= (byte) (num & 0xFF);
	 num=num>>8;  head[21]= (byte) (num & 0xFF);
	 //绾喖鐣鹃崶鎹愯杽妤傛ê瀹抽弫鏉匡拷
	 num=Y; head[22]= (byte) (num & 0xFF);
	 num=num>>8;  head[23]= (byte) (num & 0xFF);
	 num=num>>8;  head[24]= (byte) (num & 0xFF);
	 num=num>>8;  head[25]= (byte) (num & 0xFF); 
	 //绾喖鐣剧拫鍐閺夋寧鏆熼崐锟�	 j=0;
	 for (i=54;i<1078;i=i+4)
	 {
	  head[i]=head[i+1]=head[i+2]=(byte) j; 
	  head[i+3]=0;
	  j++;
	 }  
	 //閸愭瑥鍙嗛弬鍥︽婢讹拷
	 System.arraycopy(head, 0, pBmp, 0, 1078);
	 //閸愭瑥鍙嗛崶鎹愯杽閺佺増宓�
	 for(  i=0;i<Y; i++ )
	 {
		 System.arraycopy(pRaw, i*X, pBmp,1078+(Y-1-i)*X, X);
	 }	 
	 return 0;
	}
}
