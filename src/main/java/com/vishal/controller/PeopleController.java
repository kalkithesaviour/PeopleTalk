package com.vishal.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.vishal.bean.Message;
import com.vishal.bean.ReCaptchaResponse;
import com.vishal.bean.User;

@Controller
public class PeopleController {

	@Value("${google.recaptcha.verification.url}")
	private String url;

	@Value("${google.recaptcha.secret}")
	private String secret_key;

	private String URL = "http://localhost:3333/";

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Message msg;

	@GetMapping("/")
	public String home() {
		return "index";
	}

	@PostMapping("/addUser")
	public String addUser(User u, MultipartFile photo, Model m) {
		String API = "addUser/normal";

		u.setPassword(passwordEncoder.encode(u.getPassword()));

		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> data = new LinkedMultiValueMap<>();
		data.add("photo", convert(photo));
		data.add("user", u);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(data, header);

		ResponseEntity<String> result = restTemplate.postForEntity(URL + API, requestEntity, String.class);
		if (result.getBody().equalsIgnoreCase("success")) {
			m.addAttribute("addResult", u.getName() + " Added Successfully!");
		} else {
			m.addAttribute("addResult", "Email Id [" + u.getEmail() + "] Already Exist!");
		}

		return "index";
	}

	@PostMapping("/addUserGoogle")
	public String addUserGoogle(User u, MultipartFile photo, Model m, HttpSession session) {
		String API = "addUser/google";

		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> data = new LinkedMultiValueMap<>();
		data.add("photo", convert(photo));
		data.add("user", u);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(data, header);

		ResponseEntity<String> result = restTemplate.postForEntity(URL + API, requestEntity, String.class);
		if (result.getBody().equalsIgnoreCase("success")) {
			session.setAttribute("user", u);
			return "profile";
		} else {
			m.addAttribute("addResult", "Email Id [" + u.getEmail() + "] Already Exist!");
			return "index";
		}
	}

	public static FileSystemResource convert(MultipartFile file) {
		File convFile = new File(file.getOriginalFilename());
		try {
			convFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new FileSystemResource(convFile);
	}

	@PostMapping("/login")
	public String login(@RequestParam("g-recaptcha-response") String captchaResponse, String email, String password,
			Model m, HttpSession session) {
		ResponseEntity<ReCaptchaResponse> recaptchaResult = restTemplate.exchange(
				url + "?secret=" + secret_key + "&response=" + captchaResponse, HttpMethod.POST, null,
				ReCaptchaResponse.class);

		ReCaptchaResponse recaptchaResponse = recaptchaResult.getBody();

		if (recaptchaResponse.isSuccess()) {
			String API = "login/" + email;

			String dbHashedPassword = restTemplate.postForObject(URL + API, null, String.class);

			if (passwordEncoder.matches(password, dbHashedPassword)) {
				API = "getUserByAccountType/normal/" + email;
				User u = restTemplate.getForObject(URL + API, User.class);
				session.setAttribute("user", u);
				return "profile";
			} else {
				m.addAttribute("loginResult", "Login Failed!");
			}
		} else {
			m.addAttribute("loginResult", "Please verify Captcha");
		}
		return "index";
	}

	@GetMapping("/loginGoogle")
	public String loginGoogle(String email, HttpSession session) {
		String API = "getUserByAccountType/google/" + email;
		User u = restTemplate.getForObject(URL + API, User.class);
		if (u == null) {
			return "registrationforgoogleuser";
		} else {
			session.setAttribute("user", u);
			return "profile";
		}
	}

	@PostMapping("/userLogout")
	public void userLogout(HttpSession session, HttpServletResponse response) {
		session.invalidate();
		try {
			response.sendRedirect("/");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@GetMapping("/profile")
	public String profile(Model m, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			return "profile";
		} else {
			m.addAttribute("loginResult", "Please Login!");
			return "index";
		}
	}

	@GetMapping("/peopleSearch")
	public String peopleSearch(String state, String city, String area, Model m, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			m.addAttribute("state", state);
			m.addAttribute("city", city);
			m.addAttribute("area", area);

			if (area.equals("")) {
				area = "nodata";
			}

			String API = "getUserSearch/" + state + "/" + city + "/" + area + "/" + user.getEmail();

			User[] users = restTemplate.getForObject(URL + API, User[].class);
			m.addAttribute("users", users);

			return "peoplesearch";
		} else {
			m.addAttribute("loginResult", "Please Login!");
			return "index";
		}
	}

	@GetMapping("/talk")
	public String talk(String talk_email, Model m, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			if (talk_email != null) {
				session.setAttribute("talk_email", talk_email);
			} else {
				String e = (String) session.getAttribute("talk_email");
				talk_email = e;
			}

			String API = "getUserByEmail/" + talk_email;
			User talk_user = restTemplate.getForObject(URL + API, User.class);
			session.setAttribute("talk_user", talk_user);

			String API1 = "getMessages/" + user.getEmail() + "/" + talk_user.getEmail();
			Message[] sMessages = restTemplate.getForObject(URL + API1, Message[].class);
			session.setAttribute("sMessages", sMessages);

			String API2 = "getMessages/" + talk_user.getEmail() + "/" + user.getEmail();
			Message[] rMessages = restTemplate.getForObject(URL + API2, Message[].class);
			session.setAttribute("rMessages", rMessages);
			return "talk";
		} else {
			m.addAttribute("loginResult", "Please Login!");
			return "index";
		}
	}

	@PostMapping("/sendMessage")
	public void sendMessage(String message, @RequestPart("ufile") MultipartFile file, HttpSession session,
			HttpServletResponse response) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			String sEmail = user.getEmail();
			User talk_user = (User) session.getAttribute("talk_user");
			String rEmail = talk_user.getEmail();
			String fileName = file.getOriginalFilename();

			msg.setsEmail(sEmail);
			msg.setrEmail(rEmail);
			msg.setMessage(message);
			msg.setFileName(fileName);

			ResponseEntity<String> result;
			if (fileName.equals("")) {
				String API = "sendMessageWithoutFile";

				HttpEntity<Message> requestEntity = new HttpEntity<>(msg);
				result = restTemplate.postForEntity(URL + API, requestEntity, String.class);
			} else {
				String API = "sendMessage";

				HttpHeaders header = new HttpHeaders();
				header.setContentType(MediaType.MULTIPART_FORM_DATA);

				MultiValueMap<String, Object> data = new LinkedMultiValueMap<>();
				data.add("file", convert(file));
				data.add("message", msg);
				HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(data, header);

				result = restTemplate.postForEntity(URL + API, requestEntity, String.class);
			}

			if (result.getBody().equalsIgnoreCase("success")) {
				session.setAttribute("msgResult", "Message Sent Successfully!");
			} else {
				session.setAttribute("msgResult", "Message Sending Failed!");
			}

			try {
				response.sendRedirect("talk");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@GetMapping("/getPhoto")
	public void getPhoto(String email, HttpServletResponse response) {
		try {
			String API = "getPhoto/" + email;
			byte[] b = restTemplate.getForObject(URL + API, byte[].class);
			response.getOutputStream().write(b);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@GetMapping("/downloadFile")
	public void downloadFile(int id, String fileName, HttpServletResponse response) {
		try {
			String API = "downloadFile/" + id;
			byte[] b = restTemplate.getForObject(URL + API, byte[].class);
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			response.getOutputStream().write(b);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
