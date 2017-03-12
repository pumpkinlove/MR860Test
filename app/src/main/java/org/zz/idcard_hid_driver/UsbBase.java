package org.zz.idcard_hid_driver;

import java.util.HashMap;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author  chen.gs
 * @version V1.0.0.0 2015-02-10  ��װAndroid USB-Host API	
 * */
public class UsbBase {
	public static final int ERRCODE_SUCCESS	    	=  0;	   //�ɹ�
	public static final int ERRCODE_NODEVICE		= -100;    //���豸
	public static final int ERRCODE_MEMORY_OVER		= -101;	   //����������
	public static final int ERRCODE_NO_PERMISION    = -1000;   //�޷���Ȩ��
	public static final int ERRCODE_NO_CONTEXT      = -1001;   //��Context

	public static final int SHOW_MSG  = 255;                   // ��ʾ�����Ϣ�����ڵ���
	
	private int m_iSendPackageSize = 0;
	private int m_iRecvPackageSize = 0;
	
	private UsbDevice    m_usbDevice  		     = null;
	private UsbInterface m_usbInterface 		 = null;
	private UsbEndpoint  m_inEndpoint 			 = null;        //�����ݽڵ�
	private UsbEndpoint  m_outEndpoint 			 = null;        //д���ݽڵ�
	private UsbDeviceConnection m_connection     = null;
	
	private Context m_ctx          = null;
	private Handler m_fHandler     = null;

	/**
	 * ��	�ܣ�����
	 * ��	����obj - ���Դ�ӡ��Ϣ
	 * ��	�أ�
	 * */
	public void SendMsg(String obj) {
		if(ConStant.DEBUG)
		{
			Message message = new Message();
			message.what  = ConStant.SHOW_MSG;
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
	public UsbBase(Context context){
		m_ctx      = context;
		m_fHandler = null;	
		//ע�����
		regUsbMonitor();
	}
	
	public UsbBase(Context context, Handler bioHandler){
		m_ctx      = context;
		m_fHandler = bioHandler;	
		//ע�����
		regUsbMonitor();
	}
		
	/**
	 * ��	�ܣ�	����VID��PID����ȡ�����豸����
	 * ��	����	vid 	- 	VendorId��ʮ����
	 * 				pid	-	ProductId��ʮ����
	 * ��	�أ�  >=0	-	�豸������<0	-	ʧ��
	 * */
	public int getDevNum(int vid,int pid){
		if(m_ctx == null){
			return ERRCODE_NO_CONTEXT;
		}
		int iDevNum = 0; 
		UsbManager usbManager = (UsbManager)m_ctx.getSystemService(Context.USB_SERVICE);
		HashMap<String, UsbDevice> map = usbManager.getDeviceList();
		for (UsbDevice device : map.values()) {	
			if (usbManager.hasPermission(device)) {
				if ((vid == device.getVendorId()) && (pid==device.getProductId())) {
					iDevNum++;
				}
			}
			else{
				//û��Ȩ��ѯ���û��Ƿ�����Ȩ��
				PendingIntent pi = PendingIntent.getBroadcast(m_ctx, 0, new Intent(
						ACTION_USB_PERMISSION), 0);
				// �ô���ִ�к�ϵͳ����һ���Ի���ѯ���û��Ƿ�����������USB�豸��Ȩ��
				usbManager.requestPermission(device, pi); 
				return ERRCODE_NO_PERMISION;
			}
		}
		return iDevNum;
	}
	
	/**
	 * ��	�ܣ�	����VID��PID�����豸 
	 * ��	����	vid - 	VendorId��ʮ����
	 * 			pid	-	ProductId��ʮ����
	 * ��	�أ�  0	-	�ɹ�������	-	ʧ��
	 * */
	public int openDev(int vid,int pid){
		if(m_ctx == null){
			return ERRCODE_NO_CONTEXT;
		}
		UsbManager usbManager = (UsbManager)m_ctx.getSystemService(Context.USB_SERVICE);
		HashMap<String, UsbDevice> map = usbManager.getDeviceList();
		for (UsbDevice device : map.values()) {	
			if (usbManager.hasPermission(device)) {
				//SendMsg("++++++++++++++++++++++++++++++++++++++++++++");
				//SendMsg("dName: " + device.getDeviceName());
				//SendMsg("vid: " + device.getVendorId() + "\t pid: "+ device.getProductId());
				if ((vid == device.getVendorId()) && (pid==device.getProductId())) {
					m_usbDevice = device;
					m_usbInterface = m_usbDevice.getInterface(0);
					//USBEndpointΪ��д��������Ľڵ�
					m_inEndpoint  = m_usbInterface.getEndpoint(0);  //�����ݽڵ�
					m_outEndpoint = m_usbInterface.getEndpoint(1);  //д���ݽڵ�
					m_connection  = usbManager.openDevice(m_usbDevice);
					m_connection.claimInterface(m_usbInterface, true);
					m_iSendPackageSize = m_outEndpoint.getMaxPacketSize();
					m_iRecvPackageSize = m_inEndpoint.getMaxPacketSize();
					//SendMsg("-------------------------------------------");
					//SendMsg("SendPackageSize: " + m_iSendPackageSize+",RecvPackageSize: " + m_iRecvPackageSize);
					return 0;
				} else {
					continue;
				}
			}
			else{
				//û��Ȩ��ѯ���û��Ƿ�����Ȩ��
				PendingIntent pi = PendingIntent.getBroadcast(m_ctx, 0, new Intent(
						ACTION_USB_PERMISSION), 0);
				// �ô���ִ�к�ϵͳ����һ���Ի���ѯ���û��Ƿ�����������USB�豸��Ȩ��
				usbManager.requestPermission(device, pi); 
				return ERRCODE_NO_PERMISION;
			}
		}
		return ERRCODE_NODEVICE;
	}
	
	public int sendPacketSize()
	{
		return m_iSendPackageSize;
	}
	
	public int recvPacketSize()
	{
		return m_iRecvPackageSize;
	}
	
	/**
	 * 	public int bulkTransfer (UsbEndpoint endpoint, byte[] buffer, int length, int timeout)
		��	�ܣ�	Performs a bulk transaction on the given endpoint. 
				  	The direction of the transfer is determined by the direction of the endpoint
		��	����	endpoint	the endpoint for this transaction
					buffer	buffer for data to send or receive,
					length	the length of the data to send or receive
					timeout	in milliseconds
		��	�أ�	length of data transferred (or zero) for success, or negative value for failure
	 * */
	/**
	 * ��	�ܣ�	��������
	 * ��	����	bSendBuf 	- 	���������ݻ���
	 * 				iSendLen   -  ���������ݳ���
	 * 				iTimeOut	-	��ʱʱ�䣬��λ������
	 * ��	�أ�  >=0	-	�ɹ���ʵ�ʷ������ݳ��ȣ���<0	-	ʧ��
	 * */
	public int sendData(byte[] bSendBuf,int iSendLen,int iTimeOut){		
		int iRV = -1;
		if (iSendLen > bSendBuf.length) {
			return ERRCODE_MEMORY_OVER;
		}
		int iPackageSize   = sendPacketSize();
		if (iSendLen > iPackageSize) {
			return ERRCODE_MEMORY_OVER;
		}
		
		byte[] bSendBufTmp = new byte[iPackageSize];
		System.arraycopy(bSendBuf, 0, bSendBufTmp, 0,iSendLen);
		//SendMsg("�������ݰ���"+zzStringTrans.hex2str(bSendBufTmp));
		iRV = m_connection.bulkTransfer(m_outEndpoint, bSendBufTmp, iPackageSize, iTimeOut);
		//SendMsg("ʵ�ʷ������ݳ��ȣ�"+iRV);
		return iRV;
	}
	
	/**
	 * ��	�ܣ�	��������
	 * ��	����	bRecvBuf 	- 	���������ݻ���
	 * 			iRecvLen    -   ���������ݳ���
	 * 			iTimeOut	-	��ʱʱ�䣬��λ������
	 * ��	�أ�  >=0	-	�ɹ���ʵ�ʽ������ݳ��ȣ���<0	-	ʧ��
	 * */
	public int recvData(byte[] bRecvBuf,int iRecvLen,int iTimeOut){
		int iRV = -1;
		if (iRecvLen > bRecvBuf.length) {
			return ERRCODE_MEMORY_OVER;
		}
		int iPackageSize   = recvPacketSize();
		byte[] bRecvBufTmp = new byte[iPackageSize];
		for (int i=0; i<iRecvLen; i+=iPackageSize)
		{
			int nDataLen = iRecvLen-i;
			if (nDataLen > iPackageSize)
			{
				nDataLen = iPackageSize;
			}
			iRV= m_connection.bulkTransfer(m_inEndpoint, bRecvBufTmp, nDataLen, iTimeOut);
			if(iRV < 0)
			{
				//SendMsg("recvData bulkTransfer iRV="+iRV);
				return iRV;
			}
			System.arraycopy(bRecvBufTmp, 0, bRecvBuf, i,iRV);
		}
		//SendMsg("ʵ�ʽ������ݳ��ȣ�"+iRV);
		//SendMsg("�������ݰ���"+zzStringTrans.hex2str(bRecvBuf));
		return iRV;
	}
	
	/**
	 * ��	�ܣ�	�ر��豸
	 * ��	����	
	 * ��	�أ�  0	-	�ɹ�������	-	ʧ��
	 * */
	public int closeDev(){
		if(m_connection!=null){
			//SendMsg("m_connection.releaseInterface");
			m_connection.releaseInterface(m_usbInterface);
			//SendMsg("m_connection.close");
			m_connection.close();
			m_connection = null;
		}	
		return ERRCODE_SUCCESS;
	}
	
	// �ڸ�USB�豸����ͨ��֮ǰ�����Ӧ�ó������Ҫ��ȡ�û�����ɡ�
	// ����һ���㲥��������������������ڼ��������requestPermission()����ʱ��ϵͳ��������Intent����
	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbDevice device = (UsbDevice) intent
							.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						if (device != null) {
							// call method to set up device communication
						}
					} else {
						Log.d("MIAXIS", "permission denied for device "+ device);
					}
				}
			}
			//��������ɺ��豸�ġ�������֮���ֻ��߸��豸���Ƴ��ˣ�
			//ͨ������releaseInterface()��close()�ķ������ر�UseInterface��UsbDeviceConnection
			if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
				UsbDevice device = (UsbDevice) intent
						.getParcelableExtra(UsbManager.EXTRA_DEVICE);
				if (device != null) {
					// call your method that cleans up and closes communication with the device
					m_connection.releaseInterface(m_usbInterface);
					m_connection.close();
				}
			}
		}
	};

	/**
	 * ��	�ܣ�ע�����USBͨ��Ȩ�޵Ĺ㲥��������onCreate�У����ڻ�ȡ���豸ͨ�ŵ�Ȩ��
	 * ��	����
	 * ��	�أ�
	 * ��	ע���ڸ�USB�豸����ͨ��֮ǰ�����Ӧ�ó������Ҫ��ȡ�û�����ɡ�
	 *     ������Ӧ�ó���ʹ��Intent�����������ֽ����USB�豸��
	 *     �����û��������Ӧ�ó������Intent����ô�����Զ��Ľ���Ȩ�ޣ�
	 *     ���������Ӧ�ó��������豸֮ǰ��������ȷ������Ȩ�ޡ�
	 *     ��ȷ������Ȩ����ĳЩ������Ǳ���ģ�
	 *     �����Ӧ�ó����о��Ѿ������USB�豸����Ҫ�����е�һ���豸ͨ�ŵ�ʱ��
	 *     ����ͼ��һ���豸ͨ��֮ǰ�������Ҫ����Ƿ��з����豸��Ȩ�ޡ�
	 *     ��������û��ܾ�������ʸ��豸����������յ�һ������ʱ����
	 * */
	private void regUsbMonitor()
	{
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		m_ctx.registerReceiver(mUsbReceiver, filter);
	}
}
