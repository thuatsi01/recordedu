package com.exchangecurrency.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

@Controller
@RequestMapping("/")
public class SampleController {

	private static final String ACCESS_KEY = "fc74c17347fe0e8a6a4aa564bfbf1bc7";
	private static final String FROM = "from";
	private static final String TO = "to";

	@RequestMapping(method = RequestMethod.GET)
	public String printWelcome(ModelMap model) {
		String dataResponse = "";
		String strTemp = "";
		URL url = null;
		try {
			url = new URL("http://apilayer.net/api/live?access_key=fc74c17347fe0e8a6a4aa564bfbf1bc7&currencies=EUR&source=USD&format=1");
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

			while (null != (strTemp = br.readLine())) {
				dataResponse += strTemp;
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		model.addAttribute("message", dataResponse);
		return "hello";
	}

	@RequestMapping(value = "/exchange", method = RequestMethod.POST, consumes = {"multipart/form-data"}, produces={"application/json"})
	@ResponseBody
	public String exchangeCurrency(MultipartHttpServletRequest dataReceive) {
		String dataResponse = "";
		String strTemp = "";
		URL url = null;
		try {
			String urlRequest = String.format("http://apilayer.net/api/live?access_key=%s&currencies=%s&source=%s&format=%d", ACCESS_KEY, dataReceive.getParameter(TO), dataReceive.getParameter(FROM), 1);
			System.out.println(urlRequest);
			url = new URL(urlRequest);
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			while (null != (strTemp = br.readLine())) {
				dataResponse += strTemp;
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return dataResponse;
	}
}