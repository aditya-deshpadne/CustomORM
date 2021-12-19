package com.custom.orm;

import java.sql.SQLException;

public class Application {

	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
		
		TransactionHistory user1 = new TransactionHistory(123, "User1", "Credit", 10000);
		TransactionHistory user2 = new TransactionHistory(124, "User2", "Credit", 11000);
		TransactionHistory user3 = new TransactionHistory(125, "User3", "Debit", 12000);
		TransactionHistory user4 = new TransactionHistory(126, "User4", "Credit", 13000);
		TransactionHistory user5 = new TransactionHistory(127, "User5", "Debit", 14000);
		
		try {
			Hibernate<TransactionHistory> hibenate = Hibernate.getConnection();
			/*
			 * hibenate.write(user1); hibenate.write(user2); hibenate.write(user3);
			 * hibenate.write(user4); hibenate.write(user5);
			 */
			
			TransactionHistory t = hibenate.read(TransactionHistory.class, 4);
			System.out.println(t.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
