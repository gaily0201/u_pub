package cn.gaily.pub.trigger;

/**
 * <p>Title: ETLTaskFactory</P>
 * <p>Description: 数据交换任务工厂类</p>
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-29
 */
public class ETLTaskFactory {

	/**
	 * <p>方法名称：getInsertTask</p>
	 * <p>方法描述：创建插入任务</p>
	 * @return
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  创建   <p>
	 */
	public static ETLInsertTask createInsertTask(){
		return new ETLInsertTask();
	}
	
	/**
	 * <p>方法名称：createUpdateTask</p>
	 * <p>方法描述：创建更新任务</p>
	 * @return
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  创建   <p>
	 */
	public static ETLUpdateTask createUpdateTask(){
		return new ETLUpdateTask();
	}
	
	/**
	 * <p>方法名称：getInsertTask</p>
	 * <p>方法描述：创建插入任务</p>
	 * @return
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  创建   <p>
	 */
	public static ETLDeleteTask createDeleteTask(){
		return new ETLDeleteTask();
	}
}
