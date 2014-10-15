package cn.gaily.pub.util;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;


/**
 * <p>Title: FieldUpdater</P>
 * <p>Description: lucene字段匹配器</p>
 * <p>	<p>使用说明：
 * 			1、 引入lucene相关jar包
 * 			2、 lucene相关api限制，最好使用jdk1.7以上版本，否则运行会报错
 * 		</p>
 * 		 <p>手动设置要查询的字段和表名, 自动对所有字段建立索引, 模糊查询匹配度最高的数据</p>
	 * 	  使用方法：
	 * 		String[] fields = new String[]{"pk_ywcz_b","v_zfczbm","v_zfczmc"};
			String tableName = "crpas_ywcz_b";
			try {
				<p>
				//* @param fields 	  建立索引的字段
				//* @param tableName 表名
				//* @param build	  是否需要重建索引
				//* @param wherePart 筛选where语句, 带where关键字
				</p>
				UpdaterByLucene.buildLuceneIndex(fields, tableName, false, wherePart);
			} catch (IOException e) {
				e.printStackTrace();
			}
			<p>
			// * @param keyword 		搜索的关键字
			// * @param queryField		搜索字段名
			// * @param resultField		输出结果
			// * @link queryByKeyword(keyword, queryField, resultField)
			</p>
			List result = UpdaterByLucene.queryByKeyword("寻衅滋事罪", "v_zfczmc", new String[]{"pk_ywcz_b","v_zfczbm","v_zfczmc"});
			
			//测试打印结果方法
			UpdaterByLucene.printResult(result);
			
			/**
			 * 输出：   1001ZZ10000000023556
					  ZF2014_02002_635
				            寻衅滋事

   </p>
   
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-8-27
 */
public class FieldUpdater {
	
	private static Connection conn  =null;
	private static Statement st = null;
	private static ResultSet rs = null;
	
//	private static Analyzer analyzer = new SmartChineseAnalyzer(Version.LUCENE_4_9);
	private static Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
	private static IndexWriter writer = null;
	private static IndexWriterConfig config  = null;
	private static IndexReader reader = null;
	private static Directory directory = null;
	private static boolean create = true;
	private static final String INDEX_PATH = "c:\\updateIndex\\";
	
	private static String queryColSql = "SELECT column_name FROM user_tab_cols WHERE TABLE_name=?";
	private static List<String> colList = new ArrayList<String>();
	private static List<String> queryList = new ArrayList<String>();
	
	private static ArrayList<String> results = null;
	
	private static final String DB_USERNAME = "uap63";
	private static final String DB_PASSWORD = "1";
	
	public static void printResult(List<String> result){
		if(result!=null && result.size()>0){
			for(String s: result){
				System.out.println(s);
			}
		}
	}
	
	/**
	 * <p>方法名称：queryByKeyword</p>
	 * <p>方法描述：查询某一列</p>
	 * @param keyword
	 * @param column
	 * @author xiaoh
	 * @since  2014-8-26
	 * <p> history 2014-8-26 xiaoh  创建   <p>
	 */
	public static List<String> queryByKeyword(String keyword, String queryField, String[] resultField){
		QueryParser parser = new QueryParser(Version.LUCENE_4_9, queryField, analyzer);
		return query(keyword, parser, resultField);
	}

	/**
	 * <p>方法名称：query</p>
	 * <p>方法描述：lucene查询方法</p>
	 * @param keyword 关键字
	 * @param parser 查询转换器
	 * @author xiaoh
	 * @since  2014-8-26
	 * <p> history 2014-8-26 xiaoh  创建   <p>
	 */
	private static List<String> query(String keyword, QueryParser parser,String[] resultField){
		results = new ArrayList<String>();
		if(keyword==null||CommonUtil.isEmpty(keyword)){
			return null;
		}
		Vector<String> vector = null;
		List<Vector<String>> list =new ArrayList<Vector<String>>();
		
		File path = new File(INDEX_PATH);
		try{
			directory  = new SimpleFSDirectory(path);
			IndexReader reader = DirectoryReader.open(directory);
			IndexSearcher searcher  =new IndexSearcher(reader);
			if(CommonUtil.isNotEmpty(keyword)){
				if(keyword.contains("/")){
					return null;
				}
				if(keyword.contains("~")){
					return null;
				}
				if(keyword.contains("*")){
					return null;
				}
				if(keyword.contains("?")){
					return null;
				}
			}
			Query query = parser.parse(keyword);
			
			TopDocs docs = searcher.search(query, null , 1);
			ScoreDoc[] scores = docs.scoreDocs;
			int i=0;
			Document document =null;
			if(scores!=null && scores.length>0){
				for(ScoreDoc score: scores){
					document = searcher.doc(score.doc);
					if(resultField!=null && resultField.length>0){
						for(String s:resultField){
							results.add(document.get(s));
						}
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(reader!=null){
					reader.close();
				}
				if(directory!=null){
					directory.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return results;
	}
	
	
	
	/**
	 * <p>方法名称：buildLuceneIndex</p>
	 * <p>方法描述：构建索引入口</p>
	 * @param fields		字段名
	 * @param tableName 	表名
	 * @param wherePart 	wherePart语句,需要加上where关键字
	 * @author xiaoh
	 * @since  2014-8-27
	 * <p> history 2014-8-27 xiaoh  创建   <p>
	 */
	public static boolean buildLuceneIndex(String[] fields, String tableName, boolean build, String wherePart, DataSourceInfo ds) throws IOException{
		if(!build){
			return false;
		}
		queryList.clear();
		rs = getResultSet(fields, tableName, wherePart, ds);
		return buildLuceneIndex(rs);
		
	}
	
	/**
	 * <p>方法名称：buildLuceneIndex</p>
	 * <p>方法描述：构建索引入口</p>
	 * @param fields		字段名
	 * @param tableName 	表名
	 * @param wherePart 	wherePart语句,需要加上where关键字
	 * @author xiaoh
	 * @since  2014-8-27
	 * <p> history 2014-8-27 xiaoh  创建   <p>
	 */
	public static void buildLuceneIndex(String[] fields, String tableName, boolean build, String wherePart) throws IOException{
		if(!build){
			return;
		}
		rs = getResultSet(fields, tableName, wherePart);
		buildLuceneIndex(rs);
	}
	
	/**
	 * <p>方法名称：buildLuceneIndex</p>
	 * <p>方法描述：创建索引</p>
	 * @param rs2	数据库查询结果集
	 * @author xiaoh
	 * @since  2014-8-27
	 * <p> history 2014-8-27 xiaoh  创建   <p>
	 */
	private static boolean buildLuceneIndex(ResultSet rs) throws IOException {
		File path = new File(INDEX_PATH);
		if(!path.exists()){
			path.mkdirs();
		}
		File[] files = path.listFiles();
		if(files.length>0){
			create = false;
		}
		
		try {
			directory = SimpleFSDirectory.open(path);
			config = new IndexWriterConfig(Version.LUCENE_4_9, analyzer);
			
			if(create){
				config.setOpenMode(OpenMode.CREATE);
				writer = new IndexWriter(directory, config);
			}else{
				config.setOpenMode(OpenMode.CREATE_OR_APPEND);
				deleteAllIndex(path);
			}
			Document doc = null;
			while(rs!=null && rs.next()){
				doc = new Document();
				for(int i=1;i<queryList.size()+1;i++){
					if(rs.getString(i)==null||CommonUtil.isEmpty(rs.getString(i))){
						continue;
					}
					Field field = new  TextField(queryList.get(i-1), rs.getString(i), Store.YES);
					doc.add(field);
				}
				writer.addDocument(doc,analyzer);
				doc = null;
			}
			writer.commit();
			return true;
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}finally{
			if(directory!=null){
				directory.close();
			}
			if(writer!=null){
				writer.close();
			}
			JdbcUtils.release(null, null, rs);
		}
	}

	/**
	 * <p>方法名称：deleteAllIndex</p>
	 * <p>方法描述：删除目录下所有索引</p>
	 * @throws IOException
	 * @author xiaoh
	 * @since  2014-8-27
	 * <p> history 2014-8-27 xiaoh  创建   <p>
	 */
	private static void deleteAllIndex(File path) throws IOException {
		Directory directory = new SimpleFSDirectory(path);
		config.setTermIndexInterval(IndexWriterConfig.DISABLE_AUTO_FLUSH);
		
		writer = new IndexWriter(directory, config);
		try {
			writer.deleteAll();
			writer.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * <p>方法名称：getResultSet</p>
	 * <p>方法描述：根据字段名、表名后去resultSet</p>
	 * @param fields
	 * @param tableName
	 * @return
	 * @author xiaoh
	 * @since  2014-8-27
	 * <p> history 2014-8-27 xiaoh  创建   <p>
	 */
	private static ResultSet getResultSet(String[] fields, String tableName, String wherePart, DataSourceInfo ds){
		StringBuilder sql = new StringBuilder("SELECT ");
		conn = JdbcUtils.getConnection(ds.getnUserName(), ds.getnPassword(),ds.getnIp(), ds.getnName());
		//validFileds
		if(fields!=null && fields.length>0 && CommonUtil.isNotEmpty(tableName)){
			getColList(conn, tableName);
			for(int i=0;i<fields.length;i++){
				if(!colList.contains(fields[i].trim().toUpperCase())){
					throw new RuntimeException("没有找到该字段！【"+fields[i]+"】");
				}
				queryList.add(fields[i]);
			}
		}
		//build sql
		for(int i=0;i<fields.length;i++){
			sql.append(" "+fields[i]+",");
		}
		sql.delete(sql.length()-1, sql.length());
		sql.append(" FROM "+tableName);
		if(CommonUtil.isNotEmpty(wherePart)){
			sql.append(" ").append(wherePart);
		}
		
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql.toString());
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JdbcUtils.release(conn, st, null);
		}
		return rs;
	}
	
	/**
	 * <p>方法名称：getResultSet</p>
	 * <p>方法描述：根据字段名、表名后去resultSet</p>
	 * @param fields
	 * @param tableName
	 * @return
	 * @author xiaoh
	 * @since  2014-8-27
	 * <p> history 2014-8-27 xiaoh  创建   <p>
	 */
	private static ResultSet getResultSet(String[] fields, String tableName, String wherePart){
		StringBuilder sql = new StringBuilder("SELECT ");
		conn = JdbcUtils.getConnection(DB_USERNAME, DB_PASSWORD);
		//validFileds
		if(fields!=null && fields.length>0 && CommonUtil.isNotEmpty(tableName)){
			getColList(conn, tableName);
			for(int i=0;i<fields.length;i++){
				if(!colList.contains(fields[i].trim().toUpperCase())){
					throw new RuntimeException("没有找到该字段！【"+fields[i]+"】");
				}
				queryList.add(fields[i]);
			}
		}
		//build sql
		for(int i=0;i<fields.length;i++){
			sql.append(" "+fields[i]+",");
		}
		sql.delete(sql.length()-1, sql.length());
		sql.append(" FROM "+tableName);
		if(CommonUtil.isNotEmpty(wherePart)){
			sql.append(" ").append(wherePart);
		}
		
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql.toString());
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JdbcUtils.release(conn, st, null);
		}
		return rs;
	}
	
	
	
	
	private static void getColList(Connection conn, String tableName) {
		if(conn!=null){
			PreparedStatement pst = null;
			try {
				pst = conn.prepareStatement(queryColSql);
				pst.setString(1, tableName.trim().toUpperCase());
				rs = pst.executeQuery();
				String name = "";
				while(rs.next()){
					name = rs.getString(1);
					if(CommonUtil.isNotEmpty(name)){
						colList.add(name.trim().toUpperCase());
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally{
				rs = null;
				JdbcUtils.release(null, pst, rs);
			}

		}
	}


	
	private static boolean indexExist(){
		File file = new File(INDEX_PATH);
		if(file.exists()){
			return true;
		}
		return false;
	}
	
}
