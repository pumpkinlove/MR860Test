package org.zz.idcard_hid_driver;

import com.guoguang.jni.JniCall;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * @author  chen.gs
 * @version V1.0.1.20160714 ����֤�����ӿڿ⣨����HID�˵�Э�飩������MR-300������֤��
 * @see     V1.0.2.20161202 �޸�VID��PIDΪ0x10c4��0x0007������ͼƬ����ӿں�base64����ӿ�
 *          V1.0.3.20170122 �����������������IcCard���á�      
 *          V1.0.4.20170213 (1)ExeCommand���հ�����5�볬ʱ�˳�(��ֹUSB���������˳��ĳ�ʱ)
 *                          (2)�����Ƿ�����־�ӿ� mxSetTraceLevel
 *                          (3)ExeCommand���հ����ӷ�ֹͨ�������ݵ����ڴ�����ж�       
 *          V1.0.5.20170224 (1)���ο�����AntControl(1)�ӿ�
 *          				(2)�޸��հ��������յ�1���������ݳ��ȣ��������ݳ��ȼ��㻹��Ҫ���ն��ٰ�
 *                          (3)��ȡ���֤��Ϣ(����ָ����Ϣ)mxReadCardFullInfo,0�ɹ��޸�Ϊ0��1���ɹ��� 0-�ɹ�(��ָ����Ϣ)��1-�ɹ�(��ָ����Ϣ)             
 *          V1.0.6.20170306 ���ӷ�������ǰ��������USB������ 
 * */
public class IdCardDriver {

	public static byte   CMD_IDCARD_COMMAND	     = (byte) 0xB1; //ID������ָ��
	public static short  CMD_ANTCTL_CONTROL		 = (short)0xFA11;
	public static short  CMD_READIDVER_CONTROL   = (short)0xFAF0;
	public static short  CMD_READIDMSG_CONTROL   = (short)0xFA92;
	public static short  CMD_GETSAMID_CONTROL	 = (short)0x12FF;
	public static short  CMD_FindCARD_CONTROL	 = (short)0x2001;
	public static short  CMD_SELECTCARD_CONTROL	 = (short)0x2002;
	public static short  CMD_READMSG_CONTROL	 = (short)0x3001;
	public static short  CMD_READFULLMSG_CONTROL = (short)0x3010;
	
	private static int IMAGE_X             = 256;
	private static int IMAGE_Y             = 360;
	private static int IMAGE_SIZE          = IMAGE_X*IMAGE_Y;
	
	
	private static byte  CMD_GET_IMAGE	      	= (byte) 0x0A ;  //�ϴ�ͼ��
	private static byte  CMD_READ_VERSION  		= (byte) 0x0D;   //��ȡ�汾
	private static byte  CMD_GET_HALF_IMG    	= (byte) 0x14;   //�ϴ��ߵ�λѹ����ͼ��
		
	private static final int mPhotoWidth      = 102;
	private static final int mPhotoWidthBytes = (((mPhotoWidth * 3 + 3) / 4) * 4);
	private static final int mPhotoHeight     = 126;
	private static final int mPhotoSize       = (14 + 40 + mPhotoWidthBytes * mPhotoHeight);
	
	private org.zz.idcard_hid_driver.UsbBase m_usbBase;
	private Handler m_fHandler        = null;

	/******************************************************************************************
	��	�ܣ��Ƿ�����־
	��	����iTraceLevel - 0����������0����
	��	�أ�
	 ******************************************************************************************/
	public void mxSetTraceLevel(int iTraceLevel)
	{
		if(iTraceLevel!=0){
			org.zz.idcard_hid_driver.ConStant.DEBUG = true;
		}else{
			org.zz.idcard_hid_driver.ConStant.DEBUG = false;
		}
	}

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
	public IdCardDriver(Context context){
		m_usbBase = new org.zz.idcard_hid_driver.UsbBase(context);

	}
	
	public IdCardDriver(Context context, Handler bioHandler){
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
		String strVersion = "MIAXIS IdCard Driver V1.0.6.20170306";
		return strVersion;
	}
	
	/**
	 * ��	�ܣ�	��ȡ�����豸����
	 * ��	����	
	 * ��	�أ�  >=0	-	�豸������<0	-	ʧ��
	 * */
	public int mxGetDevNum()
	{
		return m_usbBase.getDevNum(org.zz.idcard_hid_driver.ConStant.VID, org.zz.idcard_hid_driver.ConStant.PID);
	}
	
	/**
	 * ��	�ܣ�	��ȡ�豸�汾��Ϣ
	 * ��	����	bVersion - �汾��Ϣ��64�ֽڣ�
	 * ��	�أ�  0	-	�ɹ�������	-	ʧ��
	 * */
	public int mxGetDevVersion(byte[] bVersion) {
		int nRet = org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
		int[] wRecvLength = new int[1];
		wRecvLength[0] = 56;
		nRet = ExeCommand(CMD_READ_VERSION,null,0,100,bVersion,wRecvLength, org.zz.idcard_hid_driver.ConStant.CMD_TIMEOUT);
		return nRet;
	}
	
	/**
	 * ��	�ܣ�	��ȡ���֤ģ��汾
	 * ��	����	bVersion - �汾��Ϣ��64�ֽڣ�
	 * ��	�أ�  0	-	�ɹ�������	-	ʧ��
	 * */
	public int mxGetIdCardModuleVersion(byte[] bVersion) {
		int iRet = org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
		iRet = GetIdCardModuleVersion(bVersion);
		if (iRet !=0x90)
		{
			return iRet;//ERRCODE_ANTENNA_ON;
		}
		return org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
	}
	
	/**
	 * ��	�ܣ�	��ȡ���֤ID
	 * ��	����	bCardId - ���֤ID��64�ֽڣ�
	 * ��	�أ�  0	-	�ɹ�������	-	ʧ��
	 * */
	public int mxReadCardId(byte[] bCardId) {	
		int iRet = org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;

		iRet = GetIdCardNo(bCardId);
		if (iRet !=0x90)
		{
			return iRet;//ERRCODE_ANTENNA_ON;
		}
		iRet = AntControl(0);
		if (iRet !=0x90)
		{
			return iRet;//ERRCODE_ANTENNA_ON;
		}
		//SendMsg("bCardId: " + zzStringTrans.hex2str(bCardId));
		return org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
	}
	
/*
	//���ն���֤��׼����
	typedef struct st_id_Card
	{
		unsigned char id_Name[30];		    //����
		unsigned char id_Sex[2];		    //�Ա� 1Ϊ�� ����ΪŮ
		unsigned char id_Rev[4];		    //����
		unsigned char id_Born[16];		    //��������
		unsigned char id_Home[70];			//סַ
		unsigned char id_Code[36];			//���֤��
		unsigned char id_RegOrg[30];		//ǩ������
		unsigned char id_ValidPeriod[32];	//��Ч����  ��ʼ����16byte ��ֹ����16byte
		unsigned char id_NewAddr[36];		//Ԥ������
		unsigned char id_pImage[1024];		//ͼƬ����
	}CARD_INFO;
*/
	/**
	 * ��	�ܣ�	��ȡ���֤��Ϣ
	 * ��	����	bCardInfo - ���֤��Ϣ��256+1024�ֽڣ�
	 * ��	�أ�  0	-	�ɹ�������	-	ʧ��
	 * */
	public int mxReadCardInfo(byte[] bCardInfo) {
		SendMsg("========================");
		SendMsg("mxReadCardInfo");
		if(bCardInfo.length<(256+1024)){
			return org.zz.idcard_hid_driver.ConStant.ERRCODE_MEMORY_OVER;
		}
		int iRet = org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
		byte[] ucCHMsg     = new  byte[256];
		byte[] ucPHMsg     = new  byte[1024];
		byte[] pucManaInfo = new  byte[256];
		int[] uiCHMsgLen   = new int[1];
		int[] uiPHMsgLen   = new int[1];
		byte[] bmp = new byte[mPhotoSize];
		SendMsg("GetSAMID");
		iRet = GetSAMID(pucManaInfo);
		if (iRet !=0x90)
		{
			AntControl(0);
			return iRet;//ERRCODE_ID_CARD_FIND;
		}
		//Ѱ��
		SendMsg("StartFindIDCard");
		iRet = StartFindIDCard(pucManaInfo);
		if (iRet !=0x9f)
		{
			iRet = StartFindIDCard(pucManaInfo);
			if (iRet !=0x9f)
			{
				//return ERRCODE_ID_CARD_FIND; //�����Ҫ������ ����Ҫ�жϷ���ֵ
			}
		}
		//ѡ��
		SendMsg("SelectIDCard");
		iRet = SelectIDCard(pucManaInfo);
		if (iRet !=0x90)
		{
			return iRet;//ERRCODE_ID_CARD_FIND; //�����Ҫ������ ����Ҫ�жϷ���ֵ
		}
		//����
		SendMsg("ReadBaseMsgUnicode");
		iRet =  ReadBaseMsgUnicode(ucCHMsg,uiCHMsgLen,ucPHMsg,uiPHMsgLen); //����
		if (iRet !=0x90)
		{
			//SendMsg("ReadBaseMsgUnicode,iRet=" +iRet);
			AntControl(0);
			return iRet;//ERRCODE_ID_CARD_READ;
		}
		for (int i = 0; i < uiCHMsgLen[0]; i++) {
			bCardInfo[i] = ucCHMsg[i];
		}
		for (int i = 0; i < uiPHMsgLen[0]; i++) {
			bCardInfo[i+256] = ucPHMsg[i];
		}
		//BMP.SaveData("/mnt/sdcard/DCIM/tttttt1111.dat", ucCHMsg, uiCHMsgLen[0]);
		//BMP.SaveData("/mnt/sdcard/DCIM/tttttt2222.dat", ucPHMsg, uiPHMsgLen[0]);
		
		//SendMsg("ucCHMsg: " +zzStringTrans.hex2str(ucCHMsg));
		//SendMsg("uiCHMsgLen: " +uiCHMsgLen[0]);
		//SendMsg("ucPHMsg: " +zzStringTrans.hex2str(ucPHMsg));
		//SendMsg("uiPHMsgLen: " +uiPHMsgLen[0]);
		SendMsg("AntControl(0)");
		AntControl(0);
		SendMsg("========================");
		return org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
	}
	
	/*
	  //���¶������֤��׼������ָ����Ϣ
		typedef struct st_id_Card_full
		{
			unsigned char id_Name[30];			//����
			unsigned char id_Sex[2];	        //�Ա� 1Ϊ�� ����ΪŮ
			unsigned char id_Rev[4];		    //����
			unsigned char id_Born[16];			//��������
			unsigned char id_Home[70];			//סַ
			unsigned char id_Code[36];		    //���֤��
			unsigned char id_RegOrg[30];	    //ǩ������
			unsigned char id_ValidPeriod[32];	//��Ч����  ��ʼ����16byte ��ֹ����16byte
			unsigned char id_NewAddr[36];		//Ԥ������
			unsigned char id_pImage[1024];		//ͼƬ����
			unsigned char id_finger[1024];      //ָ������
			unsigned char id_pBMP[38862];       //�����ͼƬ����
		}CARD_INFO_FULL;
	 */
	/**
	 * ��	�ܣ�	��ȡ���֤��Ϣ(����ָ����Ϣ)
	 * ��	����	bCardFullInfo - ���֤��Ϣ��256+1024+1024�ֽڣ�
	 * ��	�أ�  0-�ɹ�(��ָ����Ϣ)��1-�ɹ�(��ָ����Ϣ)������-ʧ��
	 * */
	public int mxReadCardFullInfo(byte[] bCardFullInfo) {
		SendMsg("========================");
		SendMsg("mxReadCardFullInfo");
		if(bCardFullInfo.length<(256+1024+1024)){
			return org.zz.idcard_hid_driver.ConStant.ERRCODE_MEMORY_OVER;
		}
		int iRet = org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
		byte[] ucCHMsg = new  byte[256];
		byte[] ucPHMsg = new  byte[1024];
		byte[] ucFPMsg  = new  byte[1024];
		byte[] pucManaInfo = new  byte[256];
		int[] uiCHMsgLen = new int[1];
		int[] uiPHMsgLen = new int[1];
		int[] uiFPMsgLen  = new int[1];
		byte[] bmp = new byte[mPhotoSize];

		SendMsg("GetSAMID");
		iRet = GetSAMID(pucManaInfo);
		if (iRet !=0x90)
		{
			AntControl(0);
			return iRet;//ERRCODE_ID_CARD_FIND;
		}
		//Ѱ��
		SendMsg("StartFindIDCard");
		iRet = StartFindIDCard(pucManaInfo);
		if (iRet !=0x9f)
		{
			iRet = StartFindIDCard(pucManaInfo);
			if (iRet !=0x9f)
			{
				return iRet; 
			}
		}
		//ѡ��
		SendMsg("SelectIDCard");
		iRet = SelectIDCard(pucManaInfo);
		if (iRet !=0x90)
		{
			SendMsg("SelectIDCard iRet="+iRet);
			return iRet; 
		}
		//����
		SendMsg("ReadFullMsgUnicode");
		iRet =  ReadFullMsgUnicode(ucCHMsg,uiCHMsgLen,ucPHMsg,uiPHMsgLen,ucFPMsg,uiFPMsgLen); //����	
		if (iRet !=0x90)
		{
			SendMsg("ReadBaseMsgUnicode,iRet=" +iRet);
			AntControl(0);
			return org.zz.idcard_hid_driver.ConStant.ERRCODE_ID_CARD_READ;
		}
		for (int i = 0; i < uiCHMsgLen[0]; i++) {
			bCardFullInfo[i] = ucCHMsg[i];
		}
		for (int i = 0; i < uiPHMsgLen[0]; i++) {
			bCardFullInfo[i+256] = ucPHMsg[i];
		}
		for (int i = 0; i < uiFPMsgLen[0]; i++) {
			bCardFullInfo[i+256+1024] = ucFPMsg[i];
		}
		
		SendMsg("AntControl(0)");
		AntControl(0);
		SendMsg("========================");
		if(uiFPMsgLen[0]==0)
			return 1;
		return org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
	}
	
	//////////////////////////////////////////////////////////////////////////
	int GetIdCardModuleVersion(byte[] bVersion)
	{
		int lRV = org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
		byte[] oPackDataBuffer = new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN]; //ʵ���ϴ˴����ܵ�64�ֽ�Ӧ�ü�ȥ����װ����ֽ��� 56�ֽ�
		int[] oPackLen = new int[1];
		oPackLen[0] = oPackDataBuffer.length;
		byte[] oRecvDataBuffer = new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN]; //ʵ���ϴ˴����ܵ�64�ֽ�Ӧ�ü�ȥ����װ����ֽ��� 56�ֽ�
		int[] oRecvLen = new int[1];
		oRecvLen[0] = oRecvDataBuffer.length;
		int[] result = new int[1];
		byte[] bSendBuf= new byte[1];

		lRV = SendIDCardPack(CMD_READIDVER_CONTROL,null,0,oPackDataBuffer,oPackLen);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		lRV =IDCardAPDU(oPackDataBuffer,oPackLen[0],100,oRecvDataBuffer,oRecvLen,500);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
	
		for (int i = 0; i < oPackDataBuffer.length; i++) {
			oPackDataBuffer[i] = 0x00;
		}
		oPackLen[0] = oPackDataBuffer.length;
		lRV =RecvIDCardPack(oRecvDataBuffer,oRecvLen[0],oPackDataBuffer,oPackLen,result);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		if ( result[0]!=0x90)
		{
			return  result[0];
		}
		for (int i = 0; i <oPackLen[0]; i++) {
			bVersion[i] = oPackDataBuffer[i];
		}
		return result[0];
	}
	
	//////////////////////////////////////////////////////////////////////////
	int GetIdCardNo(byte[] bVersion)
	{
		int lRV = org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
		byte[] oPackDataBuffer = new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN]; //ʵ���ϴ˴����ܵ�64�ֽ�Ӧ�ü�ȥ����װ����ֽ��� 56�ֽ�
		int[] oPackLen = new int[1];
		oPackLen[0] = oPackDataBuffer.length;
		byte[] oRecvDataBuffer = new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN]; //ʵ���ϴ˴����ܵ�64�ֽ�Ӧ�ü�ȥ����װ����ֽ��� 56�ֽ�
		int[] oRecvLen = new int[1];
		oRecvLen[0] = oRecvDataBuffer.length;
		int[] result = new int[1];
		byte[] bSendBuf= new byte[1];
		
		lRV = SendIDCardPack(CMD_READIDMSG_CONTROL,null,0,oPackDataBuffer,oPackLen);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		lRV =IDCardAPDU(oPackDataBuffer,oPackLen[0],100,oRecvDataBuffer,oRecvLen,500);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		
		for (int i = 0; i < oPackDataBuffer.length; i++) {
			oPackDataBuffer[i] = 0x00;
		}
		oPackLen[0] = oPackDataBuffer.length;
		lRV =RecvIDCardPack(oRecvDataBuffer,oRecvLen[0],oPackDataBuffer,oPackLen,result);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		if ( result[0]!=0x90)
		{
			return  result[0];
		}
		for (int i = 0; i <oPackLen[0]; i++) {
			bVersion[i] = oPackDataBuffer[i];
		}
		return result[0];
	}
	
	//////////////////////////////////////////////////////////////////////////
	int GetSAMID(byte[] bVersion)
	{
		int lRV = org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
		byte[] oPackDataBuffer = new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN]; //ʵ���ϴ˴����ܵ�64�ֽ�Ӧ�ü�ȥ����װ����ֽ��� 56�ֽ�
		int[] oPackLen = new int[1];
		oPackLen[0] = oPackDataBuffer.length;
		byte[] oRecvDataBuffer = new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN]; //ʵ���ϴ˴����ܵ�64�ֽ�Ӧ�ü�ȥ����װ����ֽ��� 56�ֽ�
		int[] oRecvLen = new int[1];
		oRecvLen[0] = oRecvDataBuffer.length;
		int[] result = new int[1];
		byte[] bSendBuf= new byte[1];
		
		lRV = SendIDCardPack(CMD_GETSAMID_CONTROL,null,0,oPackDataBuffer,oPackLen);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		lRV =IDCardAPDU(oPackDataBuffer,oPackLen[0],100,oRecvDataBuffer,oRecvLen,500);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		
		for (int i = 0; i < oPackDataBuffer.length; i++) {
			oPackDataBuffer[i] = 0x00;
		}
		oPackLen[0] = oPackDataBuffer.length;
		lRV =RecvIDCardPack(oRecvDataBuffer,oRecvLen[0],oPackDataBuffer,oPackLen,result);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		if ( result[0]!=0x90)
		{
			return  result[0];
		}
		for (int i = 0; i <oPackLen[0]; i++) {
			bVersion[i] = oPackDataBuffer[i];
		}
		return result[0];
	}
	//////////////////////////////////////////////////////////////////////////
	int StartFindIDCard(byte[] bVersion)
	{
		int lRV = org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
		byte[] oPackDataBuffer = new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN]; //ʵ���ϴ˴����ܵ�64�ֽ�Ӧ�ü�ȥ����װ����ֽ��� 56�ֽ�
		int[] oPackLen = new int[1];
		oPackLen[0] = oPackDataBuffer.length;
		byte[] oRecvDataBuffer = new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN]; //ʵ���ϴ˴����ܵ�64�ֽ�Ӧ�ü�ȥ����װ����ֽ��� 56�ֽ�
		int[] oRecvLen = new int[1];
		oRecvLen[0] = oRecvDataBuffer.length;
		int[] result = new int[1];
		byte[] bSendBuf= new byte[1];
	
		lRV = SendIDCardPack(CMD_FindCARD_CONTROL,null,0,oPackDataBuffer,oPackLen);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		lRV =IDCardAPDU(oPackDataBuffer,oPackLen[0],100,oRecvDataBuffer,oRecvLen,500);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
	
		for (int i = 0; i < oPackDataBuffer.length; i++) {
			oPackDataBuffer[i] = 0x00;
		}
		oPackLen[0] = oPackDataBuffer.length;
		lRV =RecvIDCardPack(oRecvDataBuffer,oRecvLen[0],oPackDataBuffer,oPackLen,result);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		if ( result[0]!=0x90)
		{
			return  result[0];
		}
		for (int i = 0; i <oPackLen[0]; i++) {
			bVersion[i] = oPackDataBuffer[i];
		}
		return result[0];
	}
	
	//////////////////////////////////////////////////////////////////////////
	int SelectIDCard(byte[] bVersion)
	{
		int lRV = org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
		byte[] oPackDataBuffer = new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN]; //ʵ���ϴ˴����ܵ�64�ֽ�Ӧ�ü�ȥ����װ����ֽ��� 56�ֽ�
		int[] oPackLen = new int[1];
		oPackLen[0] = oPackDataBuffer.length;
		byte[] oRecvDataBuffer = new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN]; //ʵ���ϴ˴����ܵ�64�ֽ�Ӧ�ü�ȥ����װ����ֽ��� 56�ֽ�
		int[] oRecvLen = new int[1];
		oRecvLen[0] = oRecvDataBuffer.length;
		int[] result = new int[1];
		byte[] bSendBuf= new byte[1];
		
		SendMsg("SendIDCardPack");
		lRV = SendIDCardPack(CMD_SELECTCARD_CONTROL,null,0,oPackDataBuffer,oPackLen);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			SendMsg("SendIDCardPack lRV="+lRV);
			return lRV;
		}
		SendMsg("IDCardAPDU");
		lRV =IDCardAPDU(oPackDataBuffer,oPackLen[0],100,oRecvDataBuffer,oRecvLen,500);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			SendMsg("IDCardAPDU lRV="+lRV);
			return lRV;
		}
		
		for (int i = 0; i < oPackDataBuffer.length; i++) {
			oPackDataBuffer[i] = 0x00;
		}
		oPackLen[0] = oPackDataBuffer.length;
		SendMsg("RecvIDCardPack");
		lRV =RecvIDCardPack(oRecvDataBuffer,oRecvLen[0],oPackDataBuffer,oPackLen,result);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			SendMsg("RecvIDCardPack lRV="+lRV);
			return lRV;
		}
		if ( result[0]!=0x90)
		{
			SendMsg("RecvIDCardPack result[0]="+result[0]);
			return  result[0];
		}
		for (int i = 0; i <oPackLen[0]; i++) {
			bVersion[i] = oPackDataBuffer[i];
		}
		return result[0];
	}
	//////////////////////////////////////////////////////////////////////////
	int ReadBaseMsgUnicode(byte[] pucCHMsg, int[] puiCHMsgLen,byte[] PucPHMsg,int[] puiPHMsgLen)
	{
		int lRV = org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
		byte[] oPackDataBuffer = new byte[org.zz.idcard_hid_driver.ConStant.CMD_BUFSIZE]; //ʵ���ϴ˴����ܵ�64�ֽ�Ӧ�ü�ȥ����װ����ֽ��� 56�ֽ�
		int[] oPackLen = new int[1];
		oPackLen[0] = oPackDataBuffer.length;
		byte[] oRecvDataBuffer = new byte[org.zz.idcard_hid_driver.ConStant.CMD_BUFSIZE]; //ʵ���ϴ˴����ܵ�64�ֽ�Ӧ�ü�ȥ����װ����ֽ��� 56�ֽ�
		int[] oRecvLen = new int[1];
		oRecvLen[0] = oRecvDataBuffer.length;
		int[] result = new int[1];
		byte[] bSendBuf= new byte[1];
		
		lRV = SendIDCardPack(CMD_READMSG_CONTROL,null,0,oPackDataBuffer,oPackLen);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		lRV =IDCardAPDU(oPackDataBuffer,oPackLen[0],100,oRecvDataBuffer,oRecvLen,500);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		
		for (int i = 0; i < oPackDataBuffer.length; i++) {
			oPackDataBuffer[i] = 0x00;
		}
		oPackLen[0] = oPackDataBuffer.length;
		lRV =RecvIDCardPack(oRecvDataBuffer,oRecvLen[0],oPackDataBuffer,oPackLen,result);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		if ( result[0]!=0x90)
		{
			return  result[0];
		}
		if (oPackLen[0] != 1295)
		{
			return org.zz.idcard_hid_driver.ConStant.ERRCODE_CRC;
		}
		for (int i = 0; i < 256; i++) {
			pucCHMsg[i] = oPackDataBuffer[i+4];
		}
		puiCHMsgLen[0] = 256;
		for (int i = 0; i < 1024; i++) {
			PucPHMsg[i] = oPackDataBuffer[i+4+256];
		}
		puiPHMsgLen[0] = 1024;
		return result[0];
	}
	
	//////////////////////////////////////////////////////////////////////////
	int ReadFullMsgUnicode(byte[] pucCHMsg, int[] puiCHMsgLen,
			byte[] PucPHMsg,int[] puiPHMsgLen,
			byte[] PucFPMsg,int[] puiFPMsgLen)
	{
		int lRV = org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
		byte[] oPackDataBuffer = new byte[org.zz.idcard_hid_driver.ConStant.CMD_BUFSIZE]; //ʵ���ϴ˴����ܵ�64�ֽ�Ӧ�ü�ȥ����װ����ֽ��� 56�ֽ�
		int[] oPackLen = new int[1];
		oPackLen[0] = oPackDataBuffer.length;
		byte[] oRecvDataBuffer = new byte[org.zz.idcard_hid_driver.ConStant.CMD_BUFSIZE]; //ʵ���ϴ˴����ܵ�64�ֽ�Ӧ�ü�ȥ����װ����ֽ��� 56�ֽ�
		int[] oRecvLen = new int[1];
		oRecvLen[0] = oRecvDataBuffer.length;
		int[] result = new int[1];
		byte[] bSendBuf= new byte[1];

		lRV = SendIDCardPack(CMD_READFULLMSG_CONTROL,null,0,oPackDataBuffer,oPackLen);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}

		lRV =IDCardAPDU(oPackDataBuffer,oPackLen[0],100,oRecvDataBuffer,oRecvLen,500);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		
		//ָ�����ݳ���
		puiFPMsgLen[0] = oRecvDataBuffer[14]*256+oRecvDataBuffer[13];
		SendMsg("puiFPMsgLen[0]="+puiFPMsgLen[0]);
		for (int i = 0; i < oPackDataBuffer.length; i++) {
			oPackDataBuffer[i] = 0x00;
		}
		oPackLen[0] = oPackDataBuffer.length;
		SendMsg("RecvIDCardPack");
		lRV =RecvIDCardPack(oRecvDataBuffer,oRecvLen[0],oPackDataBuffer,oPackLen,result);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			SendMsg("RecvIDCardPack lRV="+lRV);
			return lRV;
		}
		if ( result[0]!=0x90)
		{
			SendMsg("RecvIDCardPack result[0]="+result[0]);
			return  result[0];
		}
//		if (oPackLen[0] != 2321)
//		{
//			return ConStant.ERRCODE_CRC;
//		}
		for (int i = 0; i < 256; i++) {
			pucCHMsg[i] = oPackDataBuffer[i+4+2];
		}
		puiCHMsgLen[0] = 256;
		for (int i = 0; i < 1024; i++) {
			PucPHMsg[i] = oPackDataBuffer[i+4+2+256];
		}
		puiPHMsgLen[0] = 1024;
		for (int i = 0; i < puiFPMsgLen[0]; i++) {
			PucFPMsg[i] = oPackDataBuffer[i+4+2+256+1024];
		}
		//puiFPMsgLen[0] = 1024;
		return result[0];
	}

	//////////////////////////////////////////////////////////////////////////
	int AntControl(int dAntState)
	{
		int lRV = org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
		byte[] oPackDataBuffer = new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN]; //ʵ���ϴ˴����ܵ�64�ֽ�Ӧ�ü�ȥ����װ����ֽ��� 56�ֽ�
		int[] oPackLen = new int[1];
		oPackLen[0] = oPackDataBuffer.length;
		byte[] oRecvDataBuffer = new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN]; //ʵ���ϴ˴����ܵ�64�ֽ�Ӧ�ü�ȥ����װ����ֽ��� 56�ֽ�
		int[] oRecvLen = new int[1];
		oRecvLen[0] = oRecvDataBuffer.length;
		int[] result = new int[1];
		byte[] bSendBuf= new byte[1];
		bSendBuf[0] = (byte) dAntState;
		lRV = SendIDCardPack(CMD_ANTCTL_CONTROL,bSendBuf,1,oPackDataBuffer,oPackLen);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		lRV =IDCardAPDU(oPackDataBuffer,oPackLen[0],100,oRecvDataBuffer,oRecvLen,500);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		
		for (int i = 0; i < oPackDataBuffer.length; i++) {
			oPackDataBuffer[i] = 0x00;
		}
		oPackLen[0] = oPackDataBuffer.length;
		lRV =RecvIDCardPack(oRecvDataBuffer,oRecvLen[0],oPackDataBuffer,oPackLen,result);
		if(lRV != org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS)
		{
			return lRV;
		}
		return result[0];
	}

	//////////////////////////////////////////////////////////////////////////
	//���֤�������
	int SendIDCardPack(short IDCardCommandIDAndIDCardparam,byte[] SendDataBuffer,
			int SendLen,byte[] oPackDataBuffer,int[] oPackLen)
	{
		byte[] tempBufferData = new byte[org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN];//ʵ���ϴ˴����ܵ�64�ֽ�Ӧ�ü�ȥ����װ����ֽ��� 56�ֽ�
		int i=0;
		int offsize =0;
		byte AddCheck=0;
		short len =0;
		byte[] FlagStart = new byte[5];
		byte[] dtemp    = new byte[2];
		FlagStart[0] = (byte) 0xAA;
		FlagStart[1] = (byte) 0xAA;
		FlagStart[2] = (byte) 0xAA;
		FlagStart[3] = (byte) 0x96;
		FlagStart[4] = (byte) 0x69;
		dtemp[0]    = 0x00;
		dtemp[1]    = 0x00;
		//SendLen ����46��ȥ��ͷ�������56 ����ֱ�Ӳ�������
		if (SendLen > org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN-10 || SendLen < 0)
		{
			return org.zz.idcard_hid_driver.ConStant.ERRCODE_MEMORY_OVER;
		}
	
		//���ͷ AA AA AA 96 69
		for(i=0;i<FlagStart.length;i++)
		{
			tempBufferData[offsize+i] = FlagStart[i];
		}
		offsize = offsize +FlagStart.length;
	
		//2�ֽڵİ�����
		len = (short) (1 + 1+ SendLen + 1); //1����+1���� + ���ݳ�+ 1У���
	
		dtemp[0] = (byte) ((byte)(len/0x100)&0xFF);
		dtemp[1] = (byte) ((byte)len&0xFF);
		for(i=0;i<dtemp.length;i++)
		{
			tempBufferData[offsize+i] = dtemp[i];
		}
		offsize = offsize +dtemp.length;
		
		//1�ֽ����� +1�ֽڲ���
		for(i=0;i<dtemp.length;i++)
		{
			dtemp[i] = 0x00;
		}

		dtemp[0] = (byte) ((byte)(IDCardCommandIDAndIDCardparam>>8)&0xFF);
		dtemp[1] = (byte) ((byte)IDCardCommandIDAndIDCardparam&0xFF);
		for(i=0;i<dtemp.length;i++)
		{
			tempBufferData[offsize+i] = dtemp[i];
		}
		offsize = offsize +dtemp.length;
		//������
		if (SendLen >0 && SendLen < (org.zz.idcard_hid_driver.ConStant.DATA_BUFFER_SIZE_MIN-10))
		{
			for(i=0;i<SendLen;i++)
			{
				tempBufferData[offsize+i] = SendDataBuffer[i];
			}
			offsize = offsize +SendLen;
		}
		//����
		for (i=0; i<(len+2); i++)
		{
			AddCheck ^= tempBufferData[i+5];
		}
		tempBufferData[offsize] = (byte) AddCheck;
		offsize = offsize +1;
	
		if (oPackLen[0] < offsize)
		{
			return org.zz.idcard_hid_driver.ConStant.ERRCODE_MEMORY_OVER;
		}
		else
		{
			oPackLen[0]= (short) offsize;
			for(i=0;i<offsize;i++)
			{
				oPackDataBuffer[i] = tempBufferData[i];
			}
		}
		return org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
	}

	//////////////////////////////////////////////////////////////////////////
	//���֤���ݽ��
	//����������ݽ��н����У����Ӧ������
	int RecvIDCardPack(byte[] RecvDataBuffer,int RecvLen,
			byte[] oPackDataBuffer, int[] oPackLen,int[] oResult)
	{
		byte[] tempBufferData = new byte[org.zz.idcard_hid_driver.ConStant.CMD_BUFSIZE];
		int offsize =0;
		short len  =0;
		byte dresult          = (byte) 0xff;
		byte recvCheck     = 0x00;    //�յ�������У���
		byte currentCheck = 0x00;    //�����յ������ݼ���ĵ�ǰУ���
		byte[] FlagStart = new byte[5];
		byte[] dtemp    = new byte[2];
		byte[]  Reser    = new byte[2]; //Ӧ�������λ
		FlagStart[0] = (byte) 0xAA;
		FlagStart[1] = (byte) 0xAA;
		FlagStart[2] = (byte) 0xAA;
		FlagStart[3] = (byte) 0x96;
		FlagStart[4] = (byte) 0x69;
		dtemp[0]    = 0x00;
		dtemp[1]    = 0x00;
		Reser[0]     = 0x00;
		Reser[1]     = 0x00;
		//SendMsg("RecvIDCardPack RecvDataBuffer: " + zzStringTrans.hex2str(RecvDataBuffer));
		//SendMsg("RecvLen: " +RecvLen);
		for (int i = 0; i < FlagStart.length; i++) {
			if(RecvDataBuffer[i]!=FlagStart[i])
			{
				//SendMsg("RecvDataBuffer["+i+"]="+RecvDataBuffer[i]+"  vs  "+"FlagStart["+i+"]="+FlagStart[i]);
				return org.zz.idcard_hid_driver.ConStant.ERRCODE_CRC; //У���ͷ����
			}
		}
		offsize = offsize + 5;
		//����
		len = (short) (256*RecvDataBuffer[offsize]+RecvDataBuffer[offsize+1]);
		offsize= offsize +2;
		
//		dtemp[0] = (byte) ((byte)(len/0x100)&0xFF);
//		dtemp[1] = (byte) ((byte)len&0xFF);
		//memcpy(&len,dtemp,sizeof(dtemp));
	
		//Ӧ����λ
		Reser[0] = RecvDataBuffer[offsize];
		Reser[1] = RecvDataBuffer[offsize+1];
		for (int i = 0; i < Reser.length; i++) {
			if(Reser[i]!=0x00)
			{
				//SendMsg("Reser["+i+"]="+Reser[i]);
				return org.zz.idcard_hid_driver.ConStant.ERRCODE_CRC; //У���ͷ����
			}
		}
		
		offsize = offsize +2;
		//�������
		dresult = RecvDataBuffer[offsize];
		offsize = offsize +1;
		//��������
		if (len >4 )
		{
			for(int i=0;i<len-4;i++)
			{
				tempBufferData[i]=RecvDataBuffer[offsize+i];
			}
			offsize = offsize + len-4;
		}
	
		//����
		recvCheck = RecvDataBuffer[offsize];
	
		//���� 
		for (int i=0; i<(len+2-1); i++) //���Ȱ���У��� ����-1
		{
			currentCheck ^= RecvDataBuffer[i+5];
		}
		offsize = offsize+1;
	
		if (currentCheck != recvCheck)
		{
			return org.zz.idcard_hid_driver.ConStant.ERRCODE_CRC;
		}

		if (oPackDataBuffer!=null && oPackLen[0]>(len-4))
		{
			oPackLen[0] = (short) offsize;
			for(int i=0;i<len-4;i++)
			{
				oPackDataBuffer[i] = tempBufferData[i];
			}
		}
		else
		{
			return org.zz.idcard_hid_driver.ConStant.ERRCODE_MEMORY_OVER;
		}
		oResult[0] = dresult;
		if(dresult<0)
		{
			oResult[0] = dresult+256;
		}
		return org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
	}
	
	//////////////////////////////////////////////////////////////////////////
	int IDCardAPDU(byte[] lpSendData,int wSendLength,int iSendTime,byte[] lpRecvData,int[] io_wRecvLength,int iRecvTime)
	{
		//����ExeCommand����ð������ݷ��͵��豸
		int lRV = org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
		lRV = ExeCommand(CMD_IDCARD_COMMAND,lpSendData,wSendLength,iSendTime,lpRecvData,io_wRecvLength,iRecvTime);
		return lRV;
	}

	int ExeCommand(byte nCommandID, byte[] lpSendData, int wSendLength,
			int iSendTime, byte[] lpRecvData, int[] io_wRecvLength,
			int iRecvTime) {
		int iMaxRecvLen = io_wRecvLength[0];
		SendMsg("nCommandID:" + nCommandID);
		int iRet = org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
		// ���豸
		iRet = m_usbBase.openDev(org.zz.idcard_hid_driver.ConStant.VID, org.zz.idcard_hid_driver.ConStant.PID);
		if (iRet != 0) {
			return iRet;
		}
		
		//��USB������
		byte[] DataBuffer = new byte[org.zz.idcard_hid_driver.ConStant.CMD_DATA_BUF_SIZE];
		while(true)
		{
			iRet = m_usbBase.recvData(DataBuffer,DataBuffer.length,5);
			if(iRet!=0)
				break;
		}

		// �������ݰ�
		iRet = sendPacket(nCommandID, lpSendData, wSendLength);
		if (iRet != 0) {
			// �ر��豸
			m_usbBase.closeDev();
			return iRet;
		}
		// �������ݰ�
		byte[] bResult = new byte[1];
		byte[] bRecvBuf = new byte[org.zz.idcard_hid_driver.ConStant.CMD_DATA_BUF_SIZE];

		// ���յ�һ��
		iRet = recvPacket(bResult, bRecvBuf, io_wRecvLength,
				org.zz.idcard_hid_driver.ConStant.CMD_TIMEOUT);
		if (iRet != 0) {
			// �ر��豸
			m_usbBase.closeDev();
			return iRet;
		}
		// �ӵ�һ��������Ҫ���յ����ݳ���
		int len = 0;
		len = bRecvBuf[7] * 256 + bRecvBuf[8]+7;
		SendMsg("len=" + len);
		int packsize = len / org.zz.idcard_hid_driver.ConStant.REVC_BUFFER_SIZE_MIN;
		if (len % org.zz.idcard_hid_driver.ConStant.REVC_BUFFER_SIZE_MIN != 0) {
			packsize++;
		}
		SendMsg("packsize=" + packsize);
		byte[] outBuffer = new byte[org.zz.idcard_hid_driver.ConStant.CMD_BUFSIZE];
		int realsize = 0;
		SendMsg("io_wRecvLength[0]=" + io_wRecvLength[0]);
		if (io_wRecvLength[0] >= 2) {
			for (int i = 2; i < io_wRecvLength[0]; i++) {
				outBuffer[i - 2 + realsize] = bRecvBuf[i];
			}
			realsize = realsize + io_wRecvLength[0] - 2;// ʵ���յ�������
		} else {
			realsize = realsize;
		}
		SendMsg("realsize=" + realsize);
		// �ӵڶ�����ʼ������
		for (int k = 1; k < packsize; k++) {
			iRet = recvPacket(bResult, bRecvBuf, io_wRecvLength,
					org.zz.idcard_hid_driver.ConStant.CMD_TIMEOUT);
			if (iRet != 0) {
				// �ر��豸
				m_usbBase.closeDev();
				return iRet;
			}
			if (io_wRecvLength[0] >= 2) {
				for (int i = 2; i < io_wRecvLength[0]; i++) {
					outBuffer[i - 2 + realsize] = bRecvBuf[i];
				}
				realsize = realsize + io_wRecvLength[0] - 2;// ʵ���յ�������
			} else {
				realsize = realsize;
			}
		}

		// ��ֹͨ�������ݵ����ڴ����
		SendMsg("====realsize=" + realsize);
		SendMsg("====iMaxRecvLen=" + iMaxRecvLen);
		if (realsize > iMaxRecvLen) {
			// �ر��豸
			m_usbBase.closeDev();
			return org.zz.idcard_hid_driver.ConStant.ERRCODE_MEMORY_OVER;
		}
		if (realsize >= 2) {
			for (int i = 0; i < realsize; i++) {
				lpRecvData[i] = outBuffer[i];
			}
			io_wRecvLength[0] = realsize;
		}	
//		if(packsize>10)
//		{
//			recvPacket(bResult, bRecvBuf, io_wRecvLength,5);
//			SendMsg("recvPacket: " + zzStringTrans.hex2str(bRecvBuf));
//			SendMsg("recvPacket: " +io_wRecvLength[0]);
//		}
		// �ر��豸
		m_usbBase.closeDev();
		// SendMsg("recvPacket: " + zzStringTrans.hex2str(lpRecvData));
		// SendMsg("recvPacket: " +io_wRecvLength[0]);

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
	private int recvPacket(byte[] bResult,byte[] bRecvBuf,int[] iRecvLen,int iTimeOut){
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
		iRecvLen[0] =iDataLen-1;
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
		return org.zz.idcard_hid_driver.ConStant.ERRCODE_SUCCESS;
	}
	
	/**
	 * ��	�ܣ�	֤���ս���
	 * ��	����wlt     - ���룬����ǰ���ݣ�1024�ֽ�
	 * 			bmp		- ���룬��������ݣ�38862�ֽ�
	 * ��	�أ�  0	-	�ɹ�������	-	ʧ��
	 * */
	public int Wlt2Bmp(byte[] wlt, byte[] bmp) {
		if(bmp.length<mPhotoSize)
			return ConStant.ERRCODE_MEMORY_OVER;
		JniCall.Huaxu_Wlt2Bmp(wlt, bmp, 0);
		return 0;
	}
	
	/**
	* ��  ��: �����ݿ����base64����
	* ��  ��: 
	*  pInput - ���룬����ǰ���ݿ�
	*  inputLen -  ���룬�������ݿ飨pInput������
	*  pOutput - �����base64��������ݿ飬��СΪ�������ݵ�4/3����
	*      ������ݿ�pInput ���������ݿ�pOutput ��ʼ��ַ������ͬ
	*  outputbufsize- ����,��ű�������ݣ�pOutput���Ļ�������С
	* ��  ��: 
	*     0:���ڴ�ű�������ݵĻ���������������ʧ�ܡ�
	*     ����0����������ݳ��ȣ�ֵΪ(inputLen+2)/3*4
	*/
	public int Base64Encode(byte[] pInput, int inputLen, byte[] pOutput,int outputbufsize){
		return zzJavaBase64.JavaBase64Encode(pInput,inputLen,pOutput,outputbufsize); 
	}
	
}
