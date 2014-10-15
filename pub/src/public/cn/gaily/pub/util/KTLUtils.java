package cn.gaily.pub.util;
//
//import org.pentaho.di.core.KettleEnvironment;
//import org.pentaho.di.core.exception.KettleException;
//import org.pentaho.di.core.gui.JobTracker;
//import org.pentaho.di.core.util.EnvUtil;
//import org.pentaho.di.job.Job;
//import org.pentaho.di.job.JobEntryListener;
//import org.pentaho.di.job.JobEntryResult;
//import org.pentaho.di.job.JobMeta;
//import org.pentaho.di.job.entries.job.JobEntryJob;
//import org.pentaho.di.job.entry.JobEntryBase;
//import org.pentaho.di.trans.Trans;
//import org.pentaho.di.trans.TransMeta;
//
///**
// * <p>Title: KTLUtils</P>
// * <p>Description:KETTLE文件调用简单方法 </p>
// * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
// * @author xiaoh
// * @version 1.0
// * @since 2014-9-11
// */
public class KTLUtils {
//
//	/**
//	 * <p>方法名称：execJob</p>
//	 * <p>方法描述：执行本地Job文件</p>
//	 * @param jobName
//	 * @author xiaoh
//	 * @throws KettleException 
//	 * @since  2014-9-11
//	 * <p> history 2014-9-11 xiaoh  创建   <p>
//	 */
//	public static void execJob(String jobName) throws KettleException {
//		KettleEnvironment.init();
////		EnvUtil.environmentInit();
//		JobMeta jobMeta = new JobMeta(jobName, null);
//		Job job = new Job(null, jobMeta);
//		job.start();
//		JobTracker tracker = job.getJobTracker();
//		
//		job.waitUntilFinished();
//	}
//
//	/**
//	 * <p>方法名称：execTrans</p>
//	 * <p>方法描述：执行本地转换文件</p>
//	 * @param fileName
//	 * @author xiaoh
//	 * @throws KettleException 
//	 * @since  2014-9-11
//	 * <p> history 2014-9-11 xiaoh  创建   <p>
//	 */
//	public static void execTrans(String fileName) throws KettleException {
//		KettleEnvironment.init();
//		TransMeta transMeta = new TransMeta(fileName);
//		Trans trans = new Trans(transMeta);
//		trans.execute(null);
//		trans.waitUntilFinished();
//
//	}
}
