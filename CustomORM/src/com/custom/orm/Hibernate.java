package com.custom.orm;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicLong;
import com.custom.annotation.Column;
import com.custom.annotation.PrimaryKey;

public class Hibernate<T> {

	private Connection conn;
	private AtomicLong id = new AtomicLong(0L);

	public static <T> Hibernate<T> getConnection() throws SQLException, ClassNotFoundException {
		return new Hibernate<T>();
	}

	private Hibernate() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		this.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?" + "user=root&password=adya@root");
	}

	public void write(T t) throws IllegalArgumentException, IllegalAccessException, SQLException {
		Class<? extends Object> clss = t.getClass();
		Field[] declaredFields = clss.getDeclaredFields();
		Field pKey = null;
		ArrayList<Field> columns = new ArrayList<Field>();
		StringJoiner joiner = new StringJoiner(",");
		for (Field field : declaredFields) {
			if (field.isAnnotationPresent(PrimaryKey.class)) {
				pKey = field;
				System.out.println("The primary key is :: " + field.getName() + "\tValue:" + field.get(t));
			} else if (field.isAnnotationPresent(Column.class)) {
				joiner.add(field.getName());
				columns.add(field);
				System.out.println("Column:: " + field.getName() + "\tValue:" + field.get(t));
			}
		}
		StringTokenizer tokens = new StringTokenizer(joiner.toString(), ",");

		StringBuffer qMarks = new StringBuffer("?,");
		while (tokens.hasMoreElements()) {
			tokens.nextToken();
			qMarks.append("?,");

		}

		String qStrMarks = qMarks.substring(0, qMarks.length() - 1);
		String sql = "insert into " + clss.getSimpleName() + "(" + pKey.getName() + "," + joiner.toString() + ") "
				+ "values (" + qStrMarks + ")";
		System.out.println(sql);
		PreparedStatement statement = conn.prepareStatement(sql);
		if (pKey.getType() == long.class) {

			statement.setLong(1, id.incrementAndGet());
		}

		int index = 2;
		for (Field field : columns) {

			if (field.getType() == int.class) {

				statement.setLong(index++, field.getInt(t));
			}
			if (field.getType() == String.class) {

				statement.setString(index++, (String) field.get(t));
			}
			if (field.getType() == double.class) {

				statement.setDouble(index++, field.getDouble(t));
			}

		}
		statement.executeUpdate();
	}

	public T read(Class<T> class1, long lng) throws Exception {

		Field[] declaredFields = class1.getDeclaredFields();
		String pKey = "";
		for (Field field : declaredFields) {

			if (field.isAnnotationPresent(PrimaryKey.class)) {

				pKey = field.getName();
				break;
			}
		}

		String sql = "select * from " + class1.getSimpleName() + " where "+ pKey+" = ?";
		System.out.println(sql);
		PreparedStatement statement = conn.prepareStatement(sql);
		statement.setLong(1, lng);
		System.out.println(statement.toString());
		ResultSet res = statement.executeQuery();
		T t =class1.getConstructor().newInstance();
		if (res != null) {

			if (res.next()) {
				for (Field field : declaredFields) {

					if (field.getType() == int.class) {						
						field.set(t,res.getInt(field.getName()));
					} else if (field.getType() == String.class) {
						field.set(t,res.getString(field.getName()));						
					} else if (field.getType() == double.class) {
						field.set(t,res.getDouble(field.getName()));
					}
					else if (field.getType() == long.class) {
						field.set(t,res.getLong(field.getName()));
					}

				}
			}
		}
		return t;
	}

}
