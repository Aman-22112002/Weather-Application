package MyPackage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mysql.cj.x.protobuf.MysqlxDatatypes.Scalar.String;


@WebServlet("/MyServlet")
public class MyServlet extends HttpServlet {       
   
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				
		java.lang.String apiKey = "83bc7224b77ab08fa49c65f1751e351d";
		//get the city from the input
		java.lang.String city = request.getParameter("city");
		//Create the URL for the openWeather API request
		java.lang.String apiURL = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+apiKey;
		
		//API integration
		
		URL url = new URL(apiURL); 
		// building the connection
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		
		// reading data from network that will come in stream by stream
		InputStream inputStream = connection.getInputStream();
		InputStreamReader reader = new InputStreamReader(inputStream);
		
		// storing the data in string 
		StringBuilder responseContent = new StringBuilder();
		// Input lene ke liye from the reader,we will create the Scanner object
		Scanner scanner = new Scanner(reader);
		
		while(scanner.hasNext())
		{
			responseContent.append(scanner.nextLine());
		}
		
		scanner.close();
		
		// typeCasting from String to JSON 
		// Parsing the JSON response to extract the temperature date and humity
		Gson gson = new Gson();//it is a google library which allows you to JSON data into tree model
		JsonObject jsonObject = gson.fromJson(responseContent.toString(),JsonObject.class);
		
		//fetching the data separatedly
		//Date and time
		long dateTimeStamp = jsonObject.get("dt").getAsLong()*1000;
		java.lang.String date = new Date(dateTimeStamp).toString();
		
		//Temperature
		double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
		int temperatureCelcius = (int)(temperatureKelvin - 273.15);
		
		//Humidity
		int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
		
		//Wind Speed
		double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
		
		//Weather Condition
		java.lang.String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
		
		
		//set the date as request  request attribute (for sending to the jsp page)
		request.setAttribute("date",date);
		request.setAttribute("city", city);
		request.setAttribute("temperature", temperatureCelcius);
		request.setAttribute("weatherCondition", weatherCondition);
		request.setAttribute("humidity", humidity);
		request.setAttribute("windSpeed", windSpeed);
		request.setAttribute("weatherData",responseContent.toString());
		
		connection.disconnect();
		
		RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
		rd.forward(request, response);
		
	}

}
