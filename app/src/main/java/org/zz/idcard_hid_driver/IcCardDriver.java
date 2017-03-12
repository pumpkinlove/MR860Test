package org.zz.idcard_hid_driver;

import java.util.Calendar;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * @author  chen.gs
 * @version V1.0.1.20170122 M1�������⣺ʵ�ּ����֤����д����ĸ��ӿڡ�
 * @see    
 *     
 * */
public class IcCardDriver{
	
	private org.zz.idcard_hid_driver.UsbBase m_usbBase;
	private Handler m_fHandler        = null;
	public static byte  CMD_ICCARD_COMMAND   = (byte)0xB0; //�Ӵ�ʽIC������ָ��
	public static byte  CMD_IDCARD_COMMAND	 = (byte)0xB1; //ID������ָ��
	public static byte  CMD_ID64CARD_COMMAND = (byte)0xB2;  //ID������ָ��
	public static byte  CONTACT_CARD		 = (byte)0;
	public static byte  CONTACT_LESS_CARD	 = (byte)1;
	public static byte  CONTACT_64CARD		 = (byte)2;
	
	public static short  CMD_U_IC_CARD_AVTIVE	 = (short)0x3241;
	public static short  CMD_U_IC_CARD_S_VERIFY	 = (short)0x3242;	
	public static short  CMD_U_IC_CARD_S_READ	 = (short)0x3243;
	public static short  CMD_U_IC_CARD_S_WRITE	 = (short)0x3244;
	
	/**
	 * ��	�ܣ�����
	 * ��	����obj - ���Դ�ӡ��Ϣ
	 * ��	�أ�
	 * */
	public void SendMsg(String obj) {
		if(org.zz.idcard_hid_driver.ConStant.DEBUG)
		{
			Message message = new Message();
			message.what  = org.zz.idcard_hid_driver.ConStant.SHOW_MSG;
			message.obj   = obj;
			message.arg1  = 0;
			if (m_fHandler!=null) {
				m_fHandler.sendMessage(message);	
			}	
		}
	}
	
	/**
	 * ��	�ܣ����캯��
	 * ��	����context - Ӧ��������
	 * ��	�أ�
	 * */
	public IcCardDriver(Context context){
		m_usbBase = new org.zz.idcard_hid_driver.UsbBase(context);

	}
	
	public IcCardDriver(Context context, Handler bioHandler){
		m_fHandler = bioHandler;
		m_usbBase = new org.zz.idcard_hid_driver.UsbBase(context,bioHandler);
	}
	
	/**
	 * @author   chen.gs
	 * @category ��ȡ������Jar���汾
	 * @param    
	 * @return   Jar���汾
	 * */
	public String mxGetJarVersion() {
		// ����
		String strVersion = "MIAXIS IcCard Driver V1.0.1.20170122";
		return strVersion;
	}
		
	/**
	 * @author   chen.gs
	 * @category ����ǽӴ�ʽ�洢��
	 * @param    delaytime - ��ʱʱ�䣬��λ������
	 * 			 ATR       - ������+��UID
	 * 			 ATRLen    - ����
	 * @return   0 - �ɹ������� - ʧ��
	 * */
	public int ContactLessStorageCardActive(short delaytime,byte[] ATR,short[] ATRLen)
	{
		int lRV = org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
		byte[] oPackDataBuffer = new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN];
		short[] oPackLen = new short[1];
		oPackLen[0] = (short)oPackDataBuffer.length;
		byte[] oRecvDataBuffer = new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN];
		short[] oRecvLen = new short[1];
		oRecvLen[0] = (short)oRecvDataBuffer.length;
		byte[] SendBufferData = new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN-7];
		short SendLen =0;
		short[] Status = new short[1];
		int offsize =0 ;
		int flag = 1;
		//������������
		byte[] dtemp = new byte[2];
		//������������
		dtemp[0] = (byte) ((byte)(delaytime/0x100)&0xFF);
		dtemp[1] = (byte) ((byte)delaytime&0xFF);
		SendBufferData[0]=dtemp[0];
		SendBufferData[1]=dtemp[1];

		offsize = offsize + 2;
		SendLen = (short) offsize;
		
		SendMsg("=============================");
		SendMsg("SendBufferData:"+zzStringTrans.hex2str(SendBufferData));
		SendMsg("SendLen:"+SendLen);
		
		lRV = SendICCardPack(CMD_U_IC_CARD_AVTIVE,SendBufferData,SendLen,oPackDataBuffer,oPackLen,flag);
		if (lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			SendMsg("SendICCardPack failed,lRV="+lRV);
			return lRV;
		}
		
		SendMsg("=============================");
		SendMsg("oPackDataBuffer:"+zzStringTrans.hex2str(oPackDataBuffer));
		SendMsg("oPackLen:"+oPackLen[0]);
		
		lRV =zzICCardAPDU(CONTACT_LESS_CARD,oPackDataBuffer,oPackLen[0],100,oRecvDataBuffer,oRecvLen,100);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			SendMsg("zzICCardAPDU failed,lRV="+lRV);
			return lRV;
		}
		
		SendMsg("=============================");
		SendMsg("oRecvDataBuffer:"+zzStringTrans.hex2str(oRecvDataBuffer));
		SendMsg("oRecvLen:"+oRecvLen[0]);
		
		lRV = RecvICCardPack(oRecvDataBuffer,oRecvLen[0],Status,oPackDataBuffer,oPackLen,flag);
		if (lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			SendMsg("RecvICCardPack failed,lRV="+lRV);
			return lRV;
		}
		
		SendMsg("=============================");	
		SendMsg("oPackDataBuffer:"+zzStringTrans.hex2str(oPackDataBuffer));
		SendMsg("oPackLen:"+oPackLen[0]);
		
		if (Status[0] !=0)
		{
			SendMsg("Status[0]="+Status[0]);
			return Status[0];
		}
		
		byte[] tmp = new byte[oPackLen[0]];
		for (int j = 0; j < tmp.length; j++) {
			tmp[j]=oPackDataBuffer[j];
		}
		SendMsg("oPackDataBuffer:"+zzStringTrans.hex2str(tmp));
		
		SendMsg("ATRLen[0]="+ATRLen[0]);
		SendMsg("oPackLen[0]="+oPackLen[0]);
		if (ATR!=null && ATRLen[0] >= oPackLen[0])
		{
			
			for(int i=0;i<oPackLen[0];i++)
			{
				ATR[i]=oPackDataBuffer[i];
			}
			ATRLen[0] = oPackLen[0];
			SendMsg("ATR:"+zzStringTrans.hex2str(ATR));
		}
		else
		{
			ATRLen[0] = 0;
			return org.zz.idcard_hid_driver.ConStant.ERRCODE_MEMORY_OVER;
		}
		return org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
	}

	/**
	 * @author   chen.gs
	 * @category �ǽӴ�ʽ�洢����֤����
	 * @param    sectorNum - �����ţ� 50�� 0-15, 70�� 0-39
	 * 			 pintype   - ��Կ����
	 * 			 pin       - ��Կ��6�ֽ�
	 * @return   0 - �ɹ������� - ʧ��
	 * */
	public int ContactLessCardVerify(byte sectorNum,byte pintype,byte[] pin)
	{
		int i = 0;
		int lRV = org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
		byte[] oPackDataBuffer =  new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN];
		short[] oPackLen = new short[1];
		oPackLen[0] = (short)oPackDataBuffer.length;
		byte[] oRecvDataBuffer =  new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN];
		short[] oRecvLen = new short[1];
		oRecvLen[0] = (short)oRecvDataBuffer.length;
		byte[] SendBufferData =  new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN-7];
		short SendLen =0;
		short[] Status = new short[1];
		int offsize =0 ;
		int flag = 1;
		//������������
		SendBufferData[offsize] = sectorNum;
		offsize = offsize + 1;
		SendBufferData[offsize] = pintype;
		offsize = offsize + 1;
		for (i = 0; i < 6; i++) {
			SendBufferData[offsize+i] = pin[i];
		}
		offsize = offsize + 6;
		SendLen = (short) offsize; 
		
		SendMsg("=============================");
		SendMsg("SendBufferData:"+zzStringTrans.hex2str(SendBufferData));
		SendMsg("SendLen:"+SendLen);
		
		lRV = SendICCardPack(CMD_U_IC_CARD_S_VERIFY,SendBufferData,SendLen,oPackDataBuffer,oPackLen,flag);
		if (lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		
		SendMsg("=============================");
		SendMsg("oPackDataBuffer:"+zzStringTrans.hex2str(oPackDataBuffer));
		SendMsg("oPackLen:"+oPackLen[0]);
		
		lRV =zzICCardAPDU(CONTACT_LESS_CARD,oPackDataBuffer,oPackLen[0],100,oRecvDataBuffer,oRecvLen,100);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		
		SendMsg("=============================");
		SendMsg("oRecvDataBuffer:"+zzStringTrans.hex2str(oRecvDataBuffer));
		SendMsg("oRecvLen:"+oRecvLen[0]);
		
		lRV =RecvICCardPack(oRecvDataBuffer,oRecvLen[0],Status,oPackDataBuffer,oPackLen,flag);
		if (lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		
		SendMsg("=============================");	
		SendMsg("oPackDataBuffer:"+zzStringTrans.hex2str(oPackDataBuffer));
		SendMsg("oPackLen:"+oPackLen[0]);
		
		if (Status[0] !=0)
		{
			return Status[0];
		}
		return org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
	}
	
	/**
	 * @author   chen.gs
	 * @category �ǽӴ�ʽ�洢������
	 * @param    blockNum  - ���
	 * 			 block     - �����ݣ�16�ֽ�
	 * @return   0 - �ɹ������� - ʧ��
	 * */
	public int ContactLessCardReadBlock(byte blockNum,byte[] block)
	{
		int i = 0;
		int lRV = org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
		byte[] oPackDataBuffer =  new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN];
		short[] oPackLen = new short[1];
		oPackLen[0] = (short)oPackDataBuffer.length;
		byte[] oRecvDataBuffer =  new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN];
		short[] oRecvLen = new short[1];
		oRecvLen[0] = (short)oRecvDataBuffer.length;
		byte[] SendBufferData =  new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN-7];
		short SendLen =0;
		short[] Status = new short[1];
		int offsize =0 ;
		int flag = 1;

		//������������
		SendBufferData[offsize] = blockNum;
		offsize = offsize + 1;
		SendLen = (short) offsize; 
		
		SendMsg("=============================");
		SendMsg("SendBufferData:"+zzStringTrans.hex2str(SendBufferData));
		SendMsg("SendLen:"+SendLen);
		
		lRV = SendICCardPack(CMD_U_IC_CARD_S_READ,SendBufferData,SendLen,oPackDataBuffer,oPackLen,flag);
		if (lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		
		SendMsg("=============================");
		SendMsg("oPackDataBuffer:"+zzStringTrans.hex2str(oPackDataBuffer));
		SendMsg("oPackLen:"+oPackLen[0]);
		
		lRV = zzICCardAPDU(CONTACT_LESS_CARD,oPackDataBuffer,oPackLen[0],100,oRecvDataBuffer,oRecvLen,100);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		
		SendMsg("=============================");
		SendMsg("oRecvDataBuffer:"+zzStringTrans.hex2str(oRecvDataBuffer));
		SendMsg("oRecvLen:"+oRecvLen[0]);
		
		lRV = RecvICCardPack(oRecvDataBuffer,oRecvLen[0],Status,oPackDataBuffer,oPackLen,flag);
		if (lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		
		SendMsg("=============================");	
		SendMsg("oPackDataBuffer:"+zzStringTrans.hex2str(oPackDataBuffer));
		SendMsg("oPackLen:"+oPackLen[0]);
		
		if (Status[0] !=0)
		{
			return Status[0];
		}
		
		if (block != null && oPackLen[0]<=16)
		{
			for (i = 0; i < oPackLen[0]; i ++) {
				block[i]=oPackDataBuffer[i];
			}
		}
		
		return org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
	}

	/**
	 * @author   chen.gs
	 * @category �ǽӴ�ʽ�洢��д��
	 * @param    blockNum  - ���
	 * 			 block     - �����ݣ�16�ֽ�
	 * @return   0 - �ɹ������� - ʧ��
	 * */
	public int ContactLessCardWriteBlock(byte blockNum,byte[] block)
	{
		int i   = 0;
		int lRV = org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
		byte[] oPackDataBuffer =  new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN];
		short[] oPackLen = new short[1];
		oPackLen[0] = (short)oPackDataBuffer.length;
		byte[] oRecvDataBuffer =  new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN];
		short[] oRecvLen = new short[1];
		oRecvLen[0] = (short)oRecvDataBuffer.length;
		byte[] SendBufferData =  new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN-7];
		short SendLen =0;
		short[] Status = new short[1];
		int offsize =0 ;
		int flag = 1;
		
		//������������
		SendBufferData[offsize] = blockNum;
		offsize = offsize + 1;
		for (i = 0; i < 16;i ++) {
			SendBufferData[offsize+i] = block[i];
		}
		offsize = offsize + 16;
		SendLen = (short) offsize; 
		
		SendMsg("=============================");
		SendMsg("SendBufferData:"+zzStringTrans.hex2str(SendBufferData));
		SendMsg("SendLen:"+SendLen);
		
		lRV = SendICCardPack(CMD_U_IC_CARD_S_WRITE,SendBufferData,SendLen,oPackDataBuffer,oPackLen,flag);
		if (lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		
		SendMsg("=============================");
		SendMsg("oPackDataBuffer:"+zzStringTrans.hex2str(oPackDataBuffer));
		SendMsg("oPackLen:"+oPackLen[0]);
		
		lRV =zzICCardAPDU(CONTACT_LESS_CARD,oPackDataBuffer,oPackLen[0],100,oRecvDataBuffer,oRecvLen,100);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		
		SendMsg("=============================");
		SendMsg("oRecvDataBuffer:"+zzStringTrans.hex2str(oRecvDataBuffer));
		SendMsg("oRecvLen:"+oRecvLen[0]);
		
		lRV =RecvICCardPack(oRecvDataBuffer,oRecvLen[0],Status,oPackDataBuffer,oPackLen,flag);
		if (lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		
		SendMsg("=============================");	
		SendMsg("oPackDataBuffer:"+zzStringTrans.hex2str(oPackDataBuffer));
		SendMsg("oPackLen:"+oPackLen[0]);
		
		if (Status[0] !=0)
		{
			return Status[0];
		}
		return org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
	}

	/* ���ݱ��� */
	void EncData(byte[] lpRawData, int nRawLen, byte[] lpEncData)
	{
		int i = 0;
		for (i=0; i<nRawLen; i++)
		{
			int aaa = JUnsigned(lpRawData[i]);
			lpEncData[2*i]   = (byte) ((aaa>>4) + 0x30); 
			lpEncData[2*i+1] = (byte) ((aaa&0xF) + 0x30); 
		}
		lpEncData[2*nRawLen] = 0;
	}
	
	/* ���ݽ��� */
	void DecData(byte[] lpEncData, int nRawLen, byte[] lpRawData)
	{
		int i = 0;
		for (i=0; i<nRawLen; i++)
		{
			lpRawData[i] = (byte) (((lpEncData[2*i]-0x30)<<4) + (lpEncData[2*i+1]-0x30));
		}
	}

	int SendICCardPack(short CommandID ,byte[] SendDataBuffer,short SendLen,byte[] oPackDataBuffer,short[] oPackLen,int flag)
	{
		int i;
		byte[] bodyBufferData = new byte[org.zz.idcard_hid_driver.ConStant.CMD_BUFSIZE*2];//ʵ���ϴ˴����ܵ�64�ֽ�Ӧ�ü�ȥ����װ����ֽ��� 56�ֽ�
		byte[] tempBufferData = new byte[org.zz.idcard_hid_driver.ConStant.CMD_BUFSIZE*2];
		byte[] DataEncode     = new byte[org.zz.idcard_hid_driver.ConStant.CMD_BUFSIZE*2];
		byte packstx  = 0x02;  //ͷ
		byte packetx  = 0x03;  //β
		byte AddCheck = 0x00;  //����
		byte[] dtemp  = new byte[2];
		int offsize = 0;
		
		//���ݰ�ͷ STX
		tempBufferData[offsize] = packstx;
		offsize = offsize + 1;
		
		//���ݵ�Ԫ���� 2byte
		dtemp[0] = 0x00;
		dtemp[1] = 0x00;
		SendLen  = (short) (SendLen +2);
		dtemp[0] = (byte) ((byte)(SendLen/0x100)&0xFF);
		dtemp[1] = (byte) ((byte)SendLen&0xFF);
		for(i=0;i<dtemp.length;i++)
		{
			tempBufferData[offsize+i] = dtemp[i];
		}
		offsize = offsize +dtemp.length;
				
		//���ݵ�Ԫ��ʽ�����+����+������������
		dtemp[0] = 0x00;
		dtemp[1] = 0x00;
		dtemp[0] = (byte) ((byte)(CommandID/0x100)&0xFF);
		dtemp[1] = (byte) ((byte)CommandID&0xFF);
		for(i=0;i<dtemp.length;i++)
		{
			tempBufferData[offsize+i] = dtemp[i];
		}
		offsize = offsize +dtemp.length;
				
		//��������
		for(i=0;i<(SendLen-2);i++)
		{
			tempBufferData[offsize+i] = SendDataBuffer[i];
		}
		offsize = offsize + SendLen-2;
		
		//���� ����У��LRC
		for (i=0; i<SendLen; i++)
		{
			AddCheck ^= tempBufferData[i+3];
		}
		tempBufferData[offsize] = AddCheck;
		offsize = offsize + 1;

		//���ݰ�β ETX
		tempBufferData[offsize] = packetx;
		offsize = offsize + 1;
		
//		byte[] tmp = new byte[offsize];
//		for (int j = 0; j < tmp.length; j++) {
//			tmp[j]=tempBufferData[j];
//		}
//		SendMsg("==tempBufferData:"+zzStringTrans.hex2str(tmp));
		//////////////////////////////////////////////////////////////////////////
		//�Ƿ���
		if (flag==1)
		{
			int bodylen = offsize-2;
			for (i=0; i<bodylen; i++)
			{
				bodyBufferData[i]=tempBufferData[i+1];
			}
			EncData(bodyBufferData, bodylen,DataEncode);
			//�������
			offsize =0;
			//���ݰ�ͷ STX
			tempBufferData[offsize] = packstx;
			offsize = offsize + 1;
			//��������ݳ���Ϊ2��
			for (i=0; i<bodylen*2; i++)
			{
				tempBufferData[offsize+i]=DataEncode[i];
			}
			offsize = offsize + bodylen*2;
			//���ݰ�β ETX
			tempBufferData[offsize] = packetx;
			offsize = offsize + 1;
		}
		
		if (oPackDataBuffer != null && oPackLen[0]>=offsize ){
			for (i=0; i<offsize; i++)
			{
				oPackDataBuffer[i]=tempBufferData[i];
			}
			oPackLen[0]  = (short) offsize;
		}else{
			return org.zz.idcard_hid_driver.ConStant.ERRCODE_MEMORY_OVER;
		}	
		return org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
	} 
	
	//////////////////////////////////////////////////////////////////////////
	int RecvICCardPack(byte[] RecvDataBuffer,short RecvLen,short[] Status,byte[] oPackDataBuffer,short[] oPackLen,int flag)
	{
		int i = 0;
		int a,b;
		byte[] bodyBufferData = new byte[org.zz.idcard_hid_driver.ConStant.CMD_BUFSIZE];
		byte[] tempBufferData = new byte[org.zz.idcard_hid_driver.ConStant.CMD_BUFSIZE];
		byte[] DecDataBuffer = new byte[org.zz.idcard_hid_driver.ConStant.CMD_BUFSIZE];
		
		byte packstx  = 0x00; //��ͷ
		byte packetx  = 0x00; //��β
		byte recvCheck =0x00;    //�յ�������У���
		byte currentCheck =0x00; //�����յ������ݼ���ĵ�ǰУ���
		byte[] dtemp = new byte[2];
		int offsize  = 0;
		short len    = 0;
		int bodylen  = RecvLen-2; //�յ��İ���Ĵ�С
		//////////////////////////////////////////////////////////////////////////
		//���ݰ�ͷ STX
		packstx = RecvDataBuffer[0];
		if (packstx !=0x02)
		{
			return org.zz.idcard_hid_driver.ConStant.ERRCODE_CRC; //У��ͷ����
		}
		offsize = offsize + 1;
		
		packetx = RecvDataBuffer[RecvLen-1];
		if (packetx !=0x03)
		{
			return org.zz.idcard_hid_driver.ConStant.ERRCODE_CRC; //У��β����
		}
		
		for (i=0; i<bodylen; i++)
		{
			bodyBufferData[i]=RecvDataBuffer[i+offsize];
		}

		if (flag==1)
		{	
			byte[] tmp = new byte[bodylen];
			for (int j = 0; j < tmp.length; j++) {
				tmp[j]=bodyBufferData[j];
			}
			SendMsg("bodyBufferData:"+zzStringTrans.hex2str(tmp));

			DecData(bodyBufferData,bodylen,DecDataBuffer);
			for (i=0; i<bodylen; i++)
			{
				bodyBufferData[i]=0x00;
			}
			
			for (i=0; i<bodylen/2; i++)
			{
				bodyBufferData[i]=DecDataBuffer[i];
			}
			
			for (int j = 0; j < tmp.length; j++) {
				tmp[j]=bodyBufferData[j];
			}
			SendMsg("DecDataBuffer:"+zzStringTrans.hex2str(tmp));

		}
	
		//���ݵ�Ԫ���� 2byte
		offsize =0 ;
		dtemp[0] = bodyBufferData[1];
		dtemp[1] = bodyBufferData[0];
		offsize = offsize + 2;
	
		a = (int)dtemp[0];
		if(a<0)
		{
			a = a+256;
		}
		b = (int)dtemp[1];
		if(b<0)
		{
			b = b+256;
		}
		len = (short) (b*256+a); 
		SendMsg("len:"+len);
		//Ӧ��Ԫ��ʽ��״̬��� +״̬��� + ���������ݣ�	
		for(i=0;i<len;i++)
		{
			tempBufferData[i]=bodyBufferData[i+offsize];
		}
		offsize = offsize + len;
	
		//����У��LRC
		recvCheck=bodyBufferData[offsize];
		offsize = offsize +1;
	
		//���� ����У��LRC
		for (i=0; i<len; i++)
		{
			currentCheck ^= tempBufferData[i];
		}
	
		if (currentCheck !=recvCheck)
		{
			return org.zz.idcard_hid_driver.ConStant.ERRCODE_CRC; //У�����֤����
		}

		//dtemp[0] = tempBufferData[1];
		//dtemp[1] = tempBufferData[0];
		//memcpy(Status,dtemp,2);
		dtemp[0] = tempBufferData[1];
		dtemp[1] = tempBufferData[0];
		a = (int)dtemp[0];
		if(a<0)
		{
			a = a+256;
		}
		b = (int)dtemp[1];
		if(b<0)
		{
			b = b+256;
		}
		Status[0] = (short) (b*256+a); 
		SendMsg("Status[0]:"+Status[0]);
		for (i=0; i<len-2; i++)
		{
			oPackDataBuffer[i] = tempBufferData[i+2];
		}
		oPackLen[0] = (short) (len-2);
		return org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
	}
	
	//////////////////////////////////////////////////////////////////////////
	int zzICCardAPDU(byte cardtype,byte[] lpSendData,short wSendLength,int iSendTime,byte[] lpRecvData,short[] io_wRecvLength,int iRecvTime)
	{
		//����ExeCommand����ð������ݷ��͵��豸
		int ret = org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
		byte nCommandID =CMD_ICCARD_COMMAND;	
		if (cardtype == CONTACT_LESS_CARD)
		{
			nCommandID = CMD_IDCARD_COMMAND; //�ǽӴ�ʽ��
		}
		else if (cardtype ==CONTACT_CARD )
		{
			nCommandID = CMD_ICCARD_COMMAND; //�Ӵ�ʽ��
		}
		else if (cardtype == CONTACT_64CARD)
		{	
			nCommandID = CMD_ID64CARD_COMMAND; //�Ӵ�64ʽ��
		}
		else
		{
			return org.zz.idcard_hid_driver.ConStant.ERRCODE_CRC;
		}	
		ret = ExeCommand(nCommandID,lpSendData,wSendLength,iSendTime,lpRecvData,io_wRecvLength,iRecvTime);
		return ret;
	}	
	

	int ExeCommand(byte nCommandID, 
			byte[] lpSendData,short wSendLength,int iSendTime,
			byte[] lpRecvData,short[] io_wRecvLength,int iRecvTime)
	{
		byte[] outBuffer = new byte[org.zz.idcard_hid_driver.ConStant.CMD_BUFSIZE];
		byte[] buf= new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE+1];
		byte[] nRetCode = new byte[1];
		short[] wRecvLen = new short[1];
		wRecvLen[0] = (short) buf.length;
		int realsize = 0;
		int packsize = org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN;
		//���豸
		int iRet = org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
		//���豸
		iRet = m_usbBase.openDev(org.zz.idcard_hid_driver.ConStant.VID, org.zz.idcard_hid_driver.ConStant.PID);
		if(iRet != 0){
			SendMsg("openDev failed,iRet="+iRet);
			return org.zz.idcard_hid_driver.ConStant.ERRCODE_NODEVICE;
		}

		//�����������
		int packnum      = wSendLength/packsize;
		int lastpacksize = wSendLength % packsize;
		if (lastpacksize !=0)
		{
			packnum = packnum+1;
		}

		for (int i =0 ; i < packnum ;i++)
		{
			for (int j = 0; j < buf.length; j++) {
				buf[j]=0x00;
			}
			if (i == (packnum -1)) //���һ��
			{
				for (int j = 0; j < lastpacksize; j++) {
					buf[j]=lpSendData[j+i*packsize];
				}
				//�������ݰ�
				iRet = sendPacket(nCommandID,buf,lastpacksize);
			}
			else
			{	
				for (int j = 0; j < packsize; j++) {
					buf[j]=lpSendData[j+i*packsize];
				}
				//�������ݰ�
				iRet = sendPacket(nCommandID,buf,packsize);
			}
			if (iRet != 0) {
				SendMsg("sendPacket failed,iRet="+iRet);
				//�ر��豸
				m_usbBase.closeDev();
				return org.zz.idcard_hid_driver.ConStant.ERRCODE_IOSEND;
			}	
		}

		//�հ���������
		long  duration = -1;
		int timeout = 5000;//��ֹUSB���������˳��ĳ�ʱ
		//һֱ�յ����ݳ�ʱ
		Calendar time1 = Calendar.getInstance();
		if (iRecvTime < 2000)
		{
			iRecvTime = 2000;//����ⲿ���ݵĳ�ʱʱ��������޸�Ϊ2000
		}
		timeout = iRecvTime; //���ⲿ��ʱʱ��Ϊ׼	
		while (true)
		{		
			if (duration>=timeout)
			{
				//�ر��豸
				m_usbBase.closeDev();
				return org.zz.idcard_hid_driver.ConStant.ERRCODE_TIMEOUT;
			}
			//�������ݰ���һֱ�ȵ��հ���ʱ��ʶ�������� 
			iRet = recvPacket(nRetCode,buf,wRecvLen,iRecvTime);
			if (iRet == org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
			{
				iRecvTime = 50;//���յ���һ�����ɹ���ʱ�� ����2���Ľ��ճ�ʱ����Ϊ100����
			}
			if (iRet == org.zz.idcard_hid_driver.ConStant.ERRCODE_TIMEOUT )
			{		
				break;
			}
			SendMsg("===wRecvLen:"+wRecvLen[0]);
			if(wRecvLen[0]>0)
			{
				byte[] tmp = new byte[wRecvLen[0]];
				for (int j = 0; j < tmp.length; j++) {
					tmp[j]=buf[j];
				}
				SendMsg("tmp:"+zzStringTrans.hex2str(tmp));	
			}
			SendMsg("buf:"+zzStringTrans.hex2str(buf));			
			if (iRet != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
			{
				if(wRecvLen[0]>0)
					break;
				m_usbBase.closeDev();
				return org.zz.idcard_hid_driver.ConStant.ERRCODE_CRC;
			}
			if (wRecvLen[0]>=2)
			{
				for (int k = 0; k < wRecvLen[0]-2; k++) {
					outBuffer[k+realsize]=buf[k+2];
				}
				realsize = realsize + wRecvLen[0]-2;//ʵ���յ�������
			}else
			{
				realsize = realsize;
			}
			Calendar time2 = Calendar.getInstance();
			duration = time2.getTimeInMillis() - time1.getTimeInMillis();
		}
		SendMsg("io_wRecvLength[0]:"+io_wRecvLength[0]);	
		SendMsg("realsize:"+realsize);	
		if (io_wRecvLength[0] > 0 && realsize <= io_wRecvLength[0])
		{
			if (realsize > 0)
			{
				for (int k = 0; k < realsize; k++) {
					lpRecvData[k]=outBuffer[k];
				}				
			}
			io_wRecvLength[0] = (short) realsize;
		}
		else
		{
			m_usbBase.closeDev();
			return org.zz.idcard_hid_driver.ConStant.ERRCODE_MEMORY_OVER;
		}

		//�ر��豸
		m_usbBase.closeDev();
		return org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
	}
	
	/**
	 * ��	�ܣ�	�������ݰ�
	 * ��	����	bCmd		- 	ָ��ID
	 *         	  	bSendBuf	- 	���������ݻ��棬�����С��64�ֽ�
	 *          	iDataLen   - 	���ݳ���
	 * ����ֵ��	0	-	�ɹ�������	-	ʧ��
	 * */
	private int sendPacket(byte bCmd,byte[] bSendBuf,int iDataLen){
		int iRet = -1;
		int   offsize     = 0;
		short iCheckSum   = 0;
		byte[] DataBuffer = new byte[org.zz.idcard_hid_driver.ConStant.CMD_DATA_BUF_SIZE];
		//1�ֽڿ�ʼ��־	0x88
		DataBuffer[offsize++] = org.zz.idcard_hid_driver.ConStant.CMD_REQ_FLAG;
		//2�ֽ�SRN����ŵ���
		DataBuffer[offsize++] = 0x00;
		DataBuffer[offsize++] = 0x00;
		//2�ֽ�Length(Length shortֱ�ӿ���)
		DataBuffer[offsize++] = (byte) ((iDataLen+1) & 0xFF);
		DataBuffer[offsize++] = (byte) ((iDataLen+1) >> 8);
		//1�ֽ�����
		DataBuffer[offsize++] = bCmd;
		//����
		if (iDataLen>1)
		{
			for (int i = 0; i < iDataLen; i++) {
				DataBuffer[offsize++] = bSendBuf[i];
			}
		}	
		//2�ֽ�AddCheck
		short tmp;
		for (int i=3; i<offsize; i++)
		{
			tmp = DataBuffer[i];
			if(tmp<0)
			{
				tmp += 256;	
			}
			iCheckSum = (short) (iCheckSum+tmp);
		}
		if(iCheckSum<0)
		{
			iCheckSum += 256;	
		}
		// 2�ֽ�У���
		DataBuffer[offsize++] = (byte) (iCheckSum & 0xFF); 
		DataBuffer[offsize++] = (byte) (((byte) (iCheckSum >> 8))& 0xFF);
		//��������
		//SendMsg("sendData: " + zzStringTrans.hex2str(DataBuffer));
		//SendMsg("sendDataLen: " +offsize);
		iRet = m_usbBase.sendData(DataBuffer,DataBuffer.length, org.zz.idcard_hid_driver.ConStant.CMD_TIMEOUT);
		if(iRet < 0)
		{
			return -1;
		}
		return 0;
	}
	
	/**
	 * ��	�ܣ�	�������ݰ�
	 * ��	����	bResult     - ������
	 * 				bRecvBuf 	- 	���������ݻ��棬�����С��64�ֽ�
	 * 				iTimeOut	-	��ʱʱ�䣬��λ������
	 * ��	�أ�  0	-	�ɹ�������	-	ʧ��
	 * */
	private int recvPacket(byte[] bResult,byte[] bRecvBuf,short[] iRecvLen,int iTimeOut){
		int iRet    = -1;
		int offsize = 0;
		int iDataLen = 0;
		int a=0,b=0;
		byte[] DataBuffer = new byte[org.zz.idcard_hid_driver.ConStant.CMD_DATA_BUF_SIZE];
		byte[] SRN = new byte[2];
		short	recvCheckSum    = 0;  //�յ���У���
		short	currentCheckSum = 0;  //��ǰ���ݼ������У���
		
		iRet = m_usbBase.recvData(DataBuffer,DataBuffer.length,iTimeOut);
		
		//SendMsg("recvPacket recvData: " + zzStringTrans.hex2str(DataBuffer));
		//SendMsg("recvPacket recvLen: " +iRet);
		if (iRet < 0) {
			return iRet;
		}
		//1�ֽڿ�ʼ��ʶ 0xAA
		if (DataBuffer[offsize++] != org.zz.idcard_hid_driver.ConStant.CMD_RET_FLAG)
		{
			return org.zz.idcard_hid_driver.ConStant.ERRCODE_CRC;
		}
		//2�ֽ�SRN
		SRN[0] = DataBuffer[offsize++];
		SRN[1] = DataBuffer[offsize++];
		//2�ֽڳ���
		//ԭ��byte��ȡֵ��Χ-128~127
		a = (int)DataBuffer[offsize++];
		if(a<0)
		{
			a = a+256;
		}
		b = (int)DataBuffer[offsize++];
		if(b<0)
		{
			b = b+256;
		}
		iDataLen = b*256+a; 
		if (iDataLen> org.zz.idcard_hid_driver.ConStant.CMD_DATA_BUF_SIZE-5) {
			return org.zz.idcard_hid_driver.ConStant.ERRCODE_CRC;
		}
		//1�ֽڰ�ִ�н��
		bResult[0] = DataBuffer[offsize];
		//����(���ݳ���-1�õ�ʵ�ʳ���)
		
		//SendMsg("offsize: " + offsize);
		//SendMsg("iDataLen: " +iDataLen);
		if ((iDataLen-1)>0)
		{
			for (int i = 1; i < iDataLen; i++) {
				bRecvBuf[i-1] = DataBuffer[offsize + i];
			}	
		}
		iRecvLen[0] =(short) (iDataLen-1);
		offsize = offsize  + iDataLen;
		//�������ݰ���У���
		for (int i=3; i<offsize; i++)
		{
			a = (int)DataBuffer[i];
			if(a<0)
			{
				a = a+256;
			}
			currentCheckSum = (short) (currentCheckSum+a);
		}
		
		//2�ֽ�У���
		a = (int)DataBuffer[offsize++];
		if(a<0)
		{
			a = a+256;
		}
		b = (int)DataBuffer[offsize++];
		if(b<0)
		{
			b = b+256;
		}
		recvCheckSum = (short) (b*256+a); 
		//SendMsg(SHOW_MSG,"��"+a+",b:"+b);
		//SendMsg(SHOW_MSG,"currentCheckSum��"+currentCheckSum+",recvCheckSum:"+recvCheckSum);
		if (currentCheckSum != recvCheckSum)
		{
			return org.zz.idcard_hid_driver.ConStant.ERRCODE_CRC;
		}
		return ConStant.ERRCODE_SUCCESS;
	}
	
	  public static int JUnsigned(int x)
	  {
	    if (x>=0)
	      return x;
	    else
	      return (x+256);
	  }
}
