package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	@Override
	String search(String text) throws Exception {
		//Write your code here
		try {
			Connection con = this.getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT response FROM lab3_table WHERE concat('', ?) LIKE concat('%', keyword, '%')");
			stmt.setString(1, text);
			ResultSet rs = stmt.executeQuery();

			String rtval = null;
			while(rs.next() && rtval == null) {
				rtval = rs.getString(1);
			}

			rs.close();
			stmt.close();
			PreparedStatement stmt2 = con.prepareStatement("UPDATE lab3_table SET count = concat(count) + 1 WHERE concat('', ?) LIKE concat('%', keyword, '%')");
			stmt2.setString(1, text);
			stmt2.executeQuery();
			stmt2.close();
			con.close();
			log.info("RETURN VAL {}", rtval);
			return rtval;
		}catch(Exception e){
			log.info("EXCEPTION {}", e);
		}
		return "";

	}
	
	
	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}

}
