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
	 * @category 从指纹图象中抽取特征
	 * @param    ucImageBuf - 指向指纹图象缓冲的指针，图象缓冲为256X360+54字节
	 *           tzBuf      - 指针指向现场录入的指纹特征，长度=512字节
	 * @return   1 - 成功
	 *           0 - 失败
	 * @see      图像格式：ISO格式图像+8字节MAC值
	 * */
	public native int mxGetTz512FromISO(byte[] ucImageBuf,byte[] tzBuf);

	/**
	 * @author   chen.gs
	 * @category 从指纹图象中抽取特征
	 * @param    ucImageBuf - 指向指纹图象缓冲的指针，图象缓冲为152X200字节
	 *           tzBuf      - 指针指向现场录入的指纹特征，长度=512字节
	 * @return   1 - 成功
	 *           0 - 失败
	 * */
	public native int mxGetTz512From152X200(byte[] ucImageBuf,byte[] tzBuf);

	/**
	 * @author   chen.gs
	 * @category 从指纹图象中抽取特征
	 * @param    ucImageBuf - 指向指纹图象缓冲的指针，图象缓冲为256X304字节
	 *           tzBuf      - 指针指向现场录入的指纹特征，长度=512字节
	 * @return   1 - 成功
	 *           0 - 失败
	 * */
	public native int mxGetTz512From256X304(byte[] ucImageBuf,byte[] tzBuf);

	/**
	 * @author   chen.gs
	 * @category 从三个指纹特征中登录指纹模板
	 * @param    tzBuf1  - 指向指纹特征1的指针，长度=512字节
	 *           tzBuf2  - 指向指纹特征2的指针，长度=512字节
	 *           tzBuf3  - 指向指纹特征3的指针，长度=512字节
	 *           mbBuf   - 指向指纹模板缓冲区的指针，长度=512字节
	 * @return    > 0    - 成功，数值表示模板质量，越大质量越高（1~100）
	 * 			   0     - 失败
	 * */
	public native int mxGetMB512(byte[] tzBuf1,byte[] tzBuf2,byte[] tzBuf3,byte[] mbBuf);

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

	/**
	 * @author   chen.gs
	 * @category 根据输入指纹特征与指纹模板集合，查找匹配指纹特征的指纹模板序号
	 * @param    usersfingerdata - 输入，指纹模板集合(模板1+模板2+...+模板N),每个模板大小512字节
	 *           usersNum        - 输入，指纹模板个数（最多支持500）
	 *           fingerdata      - 输入，指纹特征，512字节字符串
	 *           level           - 输入，匹配安全等级1～5(通常设为3)
	 * @return   >=0            - 模板序号（0 ~ usersNum-1）
	 *           <0             - 失败
	 * */
	public native int mxFingerUsersMatch512(byte[] usersfingerdata,int usersNum,byte[] fingerdata,int level);

	/**
	 * @author   chen.gs
	 * @category 从指纹图象中抽取特征
	 * @param    ucImageBuf - 指向指纹图象缓冲的指针，图象缓冲为256X360字节
	 *           tzBuf      - 指针指向现场录入的指纹特征，长度=684字节
	 * @return   1 - 成功
	 *           0 - 失败
	 * */
	public native int mxGetTzBase64(byte[] ucImageBuf,byte[] tzBuf);

	/**
	 * @author   chen.gs
	 * @category 从指纹图象中抽取特征
	 * @param    ucImageBuf - 指向指纹图象缓冲的指针，图象缓冲为256X360+54字节
	 *           tzBuf      - 指针指向现场录入的指纹特征，长度=684字节
	 * @return   1 - 成功
	 *           0 - 失败
	 * @see      图像格式：ISO格式图像+8字节MAC值
	 * */
	public native int mxGetTzBase64FromISO(byte[] ucImageBuf,byte[] tzBuf);

	/**
	 * @author   chen.gs
	 * @category 从指纹图象中抽取特征
	 * @param    ucImageBuf - 指向指纹图象缓冲的指针，图象缓冲为152X200字节
	 *           tzBuf      - 指针指向现场录入的指纹特征，长度=684字节
	 * @return   1 - 成功
	 *           0 - 失败
	 * */
	public native int mxGetTzBase64From152X200(byte[] ucImageBuf,byte[] tzBuf);

	/**
	 * @author   chen.gs
	 * @category 从指纹图象中抽取特征
	 * @param    ucImageBuf - 指向指纹图象缓冲的指针，图象缓冲为256X304字节
	 *           tzBuf      - 指针指向现场录入的指纹特征，长度=684字节
	 * @return   1 - 成功
	 *           0 - 失败
	 * */
	public native int mxGetTzBase64From256X304(byte[] ucImageBuf,byte[] tzBuf);

	/**
	 * @author   chen.gs
	 * @category 从三个指纹特征中登录指纹模板
	 * @param    tzBuf1  - 指向指纹特征1的指针，长度=684字节(base64)
	 *           tzBuf2  - 指向指纹特征2的指针，长度=684字节(base64)
	 *           tzBuf3  - 指向指纹特征3的指针，长度=684字节(base64)
	 *           mbBuf   - 指向指纹模板缓冲区的指针，长度=684字节(base64)
	 * @return    > 0    - 成功，数值表示模板质量，越大质量越高（1~100）
	 * 			   0     - 失败
	 * */
	public native int mxGetMBBase64(byte[] tzBuf1,byte[] tzBuf2,byte[] tzBuf3,byte[] mbBuf);

	/**
	 * @author   chen.gs
	 * @category 对输入的两个指纹特征值进行比对
	 * @param   mbBuf  - 指向指纹模板的指针，长度=684字节(base64)
	 *          tzBuf  - 指向指纹特征的指针，长度=684字节(base64)
	 *          level  -  匹配等级
	 * @return   0 - 成功
	 *          其他 - 失败
	 * */
	public native int mxFingerMatchBase64(byte[] mbBuf,byte[] tzBuf,int level);

	/**
	 * @author   chen.gs
	 * @category 根据输入指纹特征与指纹模板集合，查找匹配指纹特征的指纹模板序号
	 * @param    usersfingerdata - 输入，指纹模板集合(模板1+模板2+...+模板N),每个模板大小684字节
	 *           usersNum        - 输入，指纹模板个数（最多支持500）
	 *           fingerdata      - 输入，指纹特征，684字节字符串
	 *           level           - 输入，匹配安全等级1～5(通常设为3)
	 * @return   >=0            - 模板序号（0 ~ usersNum-1）
	 *           <0             - 失败
	 * */
	public native int mxFingerUsersMatchBase64(byte[] usersfingerdata,int usersNum,byte[] fingerdata,int level);

}
