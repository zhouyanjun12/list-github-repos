package LsGHRepos;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class LsGHRepos {
	public static Log log = LogFactory.getLog(LsGHRepos.class);
	public static void main(String[] args) {
        Scanner in = new Scanner(System.in);		
		String flag = "y";		
		do {

			System.out.println("Please enter a username on GitHub:");
			try {
				String username = in.next();
				while(!username.matches("[a-zA-Z0-9-]+")) {
					System.out.println("Username is illegal. Please enter again:");
				    username = in.next();
				}
					
				String main_url = "https://api.github.com/users/"+username;		             
		     	String main_json = "["+loadJson(main_url)+"]";
		     	double public_repos_num = Double.parseDouble(searchJson(main_json,"public_repos").get(0));
		     	System.out.println("There are "+(int)public_repos_num+" public repositories.");
		     	System.out.println("-----------------------------------");
		     	int page_num = (int)Math.ceil(public_repos_num/30);
		     	for(int i=1;i<=page_num;i++) {
		     		String repos_url = "https://api.github.com/users/"+username+"/repos?page="+i;
		     		String repos_json = loadJson(repos_url);
		     		ArrayList<String> res = searchJson(repos_json,"name");
		     		for(int j=0;j<res.size();j++)
		     			System.out.println(res.get(j));
		     	}
	     	}catch(Exception e){
	        	log.error(e.getMessage());
	        	log.error("mainError", e);
			}finally {
				System.out.println("\nDo you want to query another username? y:Yes or any key:No");
				flag = in.next();
			}
		
		}while(flag.equals("y"));

	}
	
	public static ArrayList<String> searchJson(String json,String key) {
		ArrayList<String> res = new ArrayList<String>();
     	try{
			JSONArray jsonArray = JSONArray.fromObject(json) ;
			if(jsonArray.size() > 0 ){
				//遍历jsonArray数组，把每个对象转成json对象
				for(int i = 0 ;i < jsonArray.size() ;i ++){					
					JSONObject jsonObject = jsonArray.getJSONObject(i) ;					     
				    //如果jsonOjbect中还包含jsonObject的话，就继续使用方法 getJSONObject(key) 返回下一层的json对象
					//JSONObject sub_jsonObject = jsonObject.getJSONObject("searchinfo") ;
					res.add(jsonObject.get(key).toString());						
				}
			}			
		}catch(JSONException e){
        	log.error(e.getMessage());
        	log.error("searchJsonError", e);	
		}
     	return res;
	}

	public static String loadJson (String url) {
        StringBuilder json = new StringBuilder();
        BufferedReader in = null;
        try {
            URL urlObject = new URL(url);
            URLConnection uc = urlObject.openConnection();
            in = new BufferedReader(new InputStreamReader(uc.getInputStream(), "utf-8"));
            String inputLine = null;
            while ( (inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
        } catch (MalformedURLException e) {	
        	System.out.println("Username does not exist! Please check your username.");
        	log.error(e.getMessage());
        	log.error("loadJsonMURLError", e);
        } catch (UnknownHostException e) {
        	System.out.println("Please check your network connection.");
        	log.error(e.getMessage());
        	log.error("loadJsonUHError", e);
        }catch (NoRouteToHostException e) {
        	System.out.println("Please check your network connection.");
        	log.error(e.getMessage());
        	log.error("loadJsonNRTHError", e);
        }catch (FileNotFoundException e) {
        	System.out.println("Username does not exist! Please check your username.");
        	log.error(e.getMessage());
        	log.error("loadJsonFNFError", e);
        }catch (ConnectException e) {
        	System.out.println("Connection time out.");
        	log.error(e.getMessage());
        	log.error("loadJsonNRTHError", e);
        }catch (IOException e) {	        	
        	log.error(e.getMessage());
        	log.error("SystemIOError", e);	        	
        	
        } finally{    
	        try{             
	        	if(in!=null) {  
	        		in.close(); //关闭流    
	        		}     
            }catch(IOException e) {      
            	log.error(e.getMessage());
            	log.error("loadJsonCIOError", e);	 
            }     
        }  
        return json.toString();
    }
}
