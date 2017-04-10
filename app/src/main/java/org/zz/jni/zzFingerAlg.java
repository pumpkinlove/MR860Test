package org.zz.jni; 
public class zzFingerAlg{
	static {
		System.loadLibrary("mxFingerAlgIdCard");
	}
	/**
	 * @author   chen.gs
	 * @category 获取算法版本号
	 * @param    version – 算法版本，100字节
	 * @return    0 - 成功
	 *           其他  - 失败  	
	 * */
	public native int mxGetVersion(byte[] version);
	
	/**
	 * @author   chen.gs
	 * @category 从指纹图象中抽取特征
	 * @param    ucImageBuf - 指向指纹图象缓冲的指针，图象缓冲为256X360字节
	 *           tzBuf      - 指针指向现场录入的指纹特征，长度=512字节
	 * @return   1 - 成功
	 *           0 - 失败	
	 * */
	public native int mxGetTz512(byte[] ucImageBuf,byte[] tzBuf);
	

	/**
	 * @author   chen.gs
	 * @category 对输入的两个指纹特征值进行比对
	 * @param   mbBuf  - 指向指纹模板的指针，长度=512字节
	 *          tzBuf  - 指向指纹特征的指针，长度=512字节
	 *          level  -  匹配等级
	 * @return   0 - 成功
	 *          其他 - 失败  	
	 * */
	public native int mxFingerMatch512(byte[] mbBuf,byte[] tzBuf,int level);
	
}
