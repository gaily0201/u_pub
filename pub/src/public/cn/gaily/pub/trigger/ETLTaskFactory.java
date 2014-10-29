package cn.gaily.pub.trigger;

/**
 * <p>Title: ETLTaskFactory</P>
 * <p>Description: ���ݽ������񹤳���</p>
 * <p>Copyright: ��������������޹�˾ Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-29
 */
public class ETLTaskFactory {

	/**
	 * <p>�������ƣ�getInsertTask</p>
	 * <p>����������������������</p>
	 * @return
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  ����   <p>
	 */
	public static ETLInsertTask createInsertTask(){
		return new ETLInsertTask();
	}
	
	/**
	 * <p>�������ƣ�createUpdateTask</p>
	 * <p>����������������������</p>
	 * @return
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  ����   <p>
	 */
	public static ETLUpdateTask createUpdateTask(){
		return new ETLUpdateTask();
	}
	
	/**
	 * <p>�������ƣ�getInsertTask</p>
	 * <p>����������������������</p>
	 * @return
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  ����   <p>
	 */
	public static ETLDeleteTask createDeleteTask(){
		return new ETLDeleteTask();
	}
}
