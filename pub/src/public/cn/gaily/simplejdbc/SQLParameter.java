package cn.gaily.simplejdbc;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class SQLParameter implements Serializable {
	static final long serialVersionUID = 1118941587227355811L;

	private List<Object> paramList = new ArrayList<Object>();

	/**
	 * 默认构造函数
	 */
	public SQLParameter() {
	}

	public SQLParameter copyOfMyself() {
		SQLParameter newParam = new SQLParameter();
		newParam.paramList.addAll(this.paramList);
		return newParam;
	}

	/**
	 * 加入一个Null参数类型
	 * 
	 * @param type
	 *            参数的类型参考java.sql.Types
	 */
	public void addNullParam(int type) {
		paramList.add(null); //TODO
	}



	/**
	 * 加入一个任意对象参数，注意该参数不能为空
	 * 
	 * @param param
	 *            对象参数
	 */
	public void addParam(Object param) {
		if (param == null)
			throw new IllegalArgumentException("SQL Parameter object cannot be null, which can be replaced by NullType Object!!");
		paramList.add(param);

	}


	/**
	 * 加入一个整型参数
	 * 
	 * @param param
	 */
	public void addParam(Integer param) {
		if (param == null) {
			addNullParam(Types.INTEGER);
		} else {
			paramList.add(param);
		}
	}

	/**
	 * 加入一个字符串类型参数
	 * 
	 * @param param
	 */
	public void addParam(String param) {
		if (param == null) {
			addNullParam(Types.VARCHAR);
		} else if (param.equals("")) {
			addNullParam(Types.VARCHAR);
		} else {
			paramList.add(param);
		}
	}

	/**
	 * 加入一个整数类型参数
	 * 
	 * @param param
	 */
	public void addParam(int param) {
		paramList.add(Integer.valueOf(param));
	}

	/**
	 * 加入一个长整数类型参数
	 * 
	 * @param param
	 */
	public void addParam(long param) {
		paramList.add(Long.valueOf(param));
	}

	/**
	 * 加入一个双精度类型参数
	 * 
	 * @param param
	 */
	public void addParam(double param) {
		paramList.add(Double.valueOf(param));
	}

	/**
	 * 加入一个布尔类型参数
	 * 
	 * 
	 * @param param
	 */
	public void addParam(boolean param) {
		paramList.add(Boolean.valueOf(param));
	}

	/**
	 * 加入一个浮点类型参数
	 * 
	 * @param param
	 */
	public void addParam(float param) {
		paramList.add(Float.valueOf(param));
	}

	/**
	 * 加入一个短整数类型参数
	 * 
	 * @param param
	 */
	public void addParam(short param) {
		paramList.add(Short.valueOf(param));
	}

	/**
	 * 根据索引得到参数对象
	 * 
	 * @param index
	 *            参数的顺序索引
	 * @return
	 */
	public Object get(int index) {
		return paramList.get(index);
	}

	/**
	 * 参数的替换，用来保持参数对象的原始信息，而不用重新构造参数对象.
	 * 
	 * @param index
	 *            要替换对象的索引从0开始记数
	 * @param obj
	 */
	public void replace(int index, Object param) {
		if (param == null)
			throw new IllegalArgumentException("SQL Parameter object cannot be null, which can be replaced by NullType Object!!");
		paramList.remove(index);
		paramList.add(index, param);
	}

	/**
	 * 清除所有参数
	 */
	public void clearParams() {
		paramList.clear();
	}

	/**
	 * 得到参数的个数
	 * 
	 * @return 返回参数的个数
	 */
	public int getCountParams() {
		return paramList.size();
	}

	/**
	 * 得到所有参数集合
	 * 
	 * @return 返回参数的集合
	 */
	@SuppressWarnings("unchecked")
	public List getParameters() {
		return paramList;
	}

	public String toString() {
		return "SQLParameter--" + paramList + "";
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		final SQLParameter that = (SQLParameter) o;

		return !(paramList != null ? !paramList.equals(that.paramList)
				: that.paramList != null);

	}

	public int hashCode() {
		return (paramList != null ? paramList.hashCode() : 0);
	}

}
