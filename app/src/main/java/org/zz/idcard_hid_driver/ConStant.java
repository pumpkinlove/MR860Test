package org.zz.idcard_hid_driver;

public class ConStant {
	public static boolean DEBUG  = false;
	//VID、PID
//	public static int VID         = 0x203B;
//	public static int PID         = 0x0101;
	public static int VID         = 0x10c4;
	public static int PID         = 0x0007;
	public static int   SHOW_MSG  = 255;      // 显示结果信息，用于调试
	public static int   DATA_BUFFER_SIZE	 	 = 64;	  // HID读写数据包大小
	public static int   DATA_BUFFER_SIZE_MIN	 = 56;	  // 套用协议后去掉包头的数据缓冲区大小(HID传输能力大小决定)
	public static int   REVC_BUFFER_SIZE_MIN	 = 54;	  // 增加2字节包序号
	public static int   CMD_BUFSIZE              = 8200;  // 4095
	
	//Commond
	public static int   CMD_DATA_BUF_SIZE		= 64;		     //HID读写数据包大小
	public static int   CMD_TIMEOUT     		= 1000;			 //等待时间
	public static byte  CMD_REQ_FLAG 			= (byte) 0x88; 	 //命令包头
	public static byte  CMD_RET_FLAG	      	= (byte) 0xAA;	 //应答包头
		
	//Error Code
	public static int  ERRCODE_SUCCESS			=0;		//成功
	public static int  ERRCODE_NODEVICE			=1;	//无设备
	public static int  ERRCODE_INDEXERR			=2;	//有设备，但是当前序号下无设备
	public static int  ERRCODE_TIMEOUT			=3;	//超时
	public static int  ERRCODE_CANCEL			=4;	//用户取消
	public static int  ERRCODE_DEVBUSY			=5;	//设备繁忙
	public static int  ERRCODE_UPIMAGE			=6;	//传感器上传图像失败
	public static int  ERRCODE_DEVLIST			=7;	//获取windows HID设备列表失败
	public static int  ERRCODE_IOSEND			=8;	//IO通信发送数据包失败
	public static int  ERRCODE_IORECV			=9;	//IO通信接收数据包失败
	public static int  ERRCODE_HANDLE_NULL		=10;	//设备访问句柄非法
	public static int  ERRCODE_CRC				=11;	//数据校验错误
	public static int  ERRCODE_MEMORY_OVER  	=12;	//缓冲区不足
	public static int  ERRCODE_ID_CARD_FIND 	=13;	//身份证寻卡失败
	public static int  ERRCODE_ID_CARD_READ 	=14;   //身份证读卡失败
	public static int  ERRCODE_ANTENNA_ON   	=15;   //天线开启失败
	public static int  ERRCODE_TCPIOSEND	 	=16;	//TCPIO通信发送数据包失败
	public static int  ERRCODE_TCPIORECV	 	=17;	//TCPIO通信接收数据包失败
	public static int  ERRCODE_TCPSERVER_INI 	=18;	//TCP服务器初始化失败
	public static int  ERRCODE_TCP_STATE	 	=19;	//未连接到TCP服务器
	public static int  ERRCODE_SAVE_IDBMP	 	=20;	//身份证信息保存为图片失败(内存载入错误)
	public static int  ERRCODE_READ_ID_MODULE 	=21;	//读取二代证模块版本失败
	public static int  ERRCODE_RESET_ID			=22;	//二代证模块复位失败
	public static int  ERRCODE_ID_MSG			=23;	//获取身份证唯一序列号失败
	public static int  ERRCODE_ANTENNA_OFF   	=24;   //天线关闭失败
}
