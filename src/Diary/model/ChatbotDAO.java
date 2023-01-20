package Diary.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import org.json.simple.JSONObject;

import Diary.Controller.Aircon;

public class ChatbotDAO {
	//---------------------필드선언 및 초기화 --------------------
	private static Connection conn;
	private static ChatbotDAO dao = new ChatbotDAO();
	private String memData;
	//---------------------필드선언 및 초기화 --------------------
	// -----------------싱글톤 작업 --------------------
	private ChatbotDAO() {
	}
	public static ChatbotDAO getInstance() {
		return dao;
	}
	private Connection getConnection() {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@jikwang.net:15210/xe","green","1234");
		} catch (Exception e) {
			System.out.println("Connection 생성시 예외 발생함 . : " + e.getMessage());
		}
		return conn;
	}
	public String response(String requestText) {
		memData = requestText;
		String result = null;
		JSONObject jsonObj;
		try {
			jsonObj = Aircon.airCon();
		conn = getConnection();
		if(requestText.contains("미세먼지")) {
			result = jsonObj.get("stationName") +"" + jsonObj.get("dataTime") + " 기준\n미세먼지(PM10) 농도 : " + jsonObj.get("pm10Value") +
					"\n" + "초미세먼지(PM2.5) 농도 : " + jsonObj.get("pm25Value") + "입니다.";
			return result;
		}
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from chatbot");
			while(rs.next()) {
					if(requestText.contains(rs.getString("request"))) {
					result = rs.getString("response");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	public void addWord(String text) {
		conn = getConnection();
		try {
			PreparedStatement pstmt = conn.prepareStatement("Insert into CHATBOT(request,response) values(?,?)");
			pstmt.setString(1, memData);
			pstmt.setString(2, text);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public int list() {
		conn = getConnection();
		int result = 0;
			Statement stmt;
			try {
				stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) as cnt from chatbot");
			rs.next();
			result = rs.getInt("cnt");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		return result;
	}
}