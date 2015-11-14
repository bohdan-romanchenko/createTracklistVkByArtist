import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.Scanner;

public class createTracklist{
	public static String[] titles;
	public static String artist;
	public static long countOfSongs;

	public static void main(String[] args) throws URISyntaxException, IOException, ParseException{

		//uncomment to read from command line or not to input in console
		//		String artist = args[0];
		//		String pathTracklist = args[1];
		Scanner scanner = new Scanner(System.in);
		System.out.print("Input artist name : ");
		artist = scanner.nextLine();
		System.out.print("Input name and path where you want to save tracklist(.m3u file) : ");
		String pathTracklist = scanner.nextLine();
		System.out.print("Input count of songs you want : ");
		countOfSongs = scanner.nextLong();

		String ACCESS_TOKEN = "";    //take access token from application vk
		String YOUR_ID_VK = "";
		String urlForVk = "https://api.vk.com/method/audio.search?&" +
				"oid=" + YOUR_ID_VK + "need_user=0&" +
				"q=" + artist + "&auto_complete=0&" +
				"lyrics=0&performer_only=1&" +
				"sort=2&search_own=0&offset=1&count=300&" +
				"access_token=" + ACCESS_TOKEN;
		parseAndSaveTracklist(urlForVk, pathTracklist);
	}

	private static String getJSON(String urlIn){
		try{
			URL url = new URL(urlIn);
			java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();
			con.setConnectTimeout(334);
			con.connect();
			int resp = con.getResponseCode();
			if(resp == 200 || resp == 6){
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String line;
				StringBuilder sb = new StringBuilder();
				while((line = br.readLine()) != null){
					sb.append(line);
					sb.append("\n");
				}
				br.close();
				return sb.toString();
			} else
				System.out.println("Error " + resp);
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private static void parseAndSaveTracklist(String url, String pathTracklist){
		JSONParser pars = new JSONParser();
		String jsonFromVk = getJSON(url);
		try {
			Object objectFinal = pars.parse(jsonFromVk);
			JSONObject objJsonFromVk = (JSONObject) objectFinal;
			JSONArray objectInJson = (JSONArray) objJsonFromVk.get("response");
			PrintWriter writer = new PrintWriter(pathTracklist);
			writer.println("#EXTM3U");
			for(int i = 0; i < countOfSongs; i++){
				JSONObject arrayInNumber = (JSONObject) objectInJson.get(i + 1);
					writer.println("#EXTINF:" + arrayInNumber.get("duration") + "," +
							(artist.trim()) + " - " +
							((String)arrayInNumber.get("title")).trim());
					String URL = (String) arrayInNumber.get("url");
					URL = URL.replace("https", "http"); //and remake https to http
					String[] cache = URL.split("\\?");  //to del everything after ? symbol
					URL = cache[0];
					writer.println(URL);
			}
			writer.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
