package io.wedeploy.controller;

import com.liferay.portal.configuration.EnvVariableDecoder;
import com.liferay.portal.configuration.EnvVariableEncoder;

import com.wedeploy.api.ApiClient;
import com.wedeploy.api.WeDeploy;
import com.wedeploy.api.sdk.Response;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Controller
@EnableAutoConfiguration
@RequestMapping("/")
public class WeDeployController extends WebMvcConfigurerAdapter {

    public WeDeployController() {
		Map<String, String> env = System.getenv();

		while (dbUrl == null) {
			if (env.containsKey("WEDEPLOY_DB_URL")) {
				dbUrl = env.get("WEDEPLOY_DB_URL");
			}
			else {
				System.out.println(
					"Please set up an environment variable with the URL to " +
						"the datastore service, with key WEDEPLOY_DB_URL");

				try {
					Thread.sleep(5000);
				}
				catch (InterruptedException e) {
					throw new RuntimeException(
						"Cannot interrupt process while waiting for proper " +
							"environment configuration");
				}
			}
		}
    }

    public static void main(String[] args) {
        SpringApplication.run(WeDeployController.class, args);
    }

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		configurer.setUseSuffixPatternMatch(false);
	}

	@Override
	public void configureContentNegotiation(
		ContentNegotiationConfigurer configurer) {

		configurer.favorPathExtension(false);
	}

    @RequestMapping(value = "/decode/{key}", method = RequestMethod.GET)
    public String decode(Model model, @PathVariable("key") String key) {
		if (key == null || key.isEmpty() ||
			!key.startsWith(Constants.ENV_OVERRIDE_PREFIX)) {

			model.addAttribute(
				"error", "Liferay Env Vars start with LIFERAY_ prefix");
		}
		else {
			String envKey = key.substring(
				Constants.ENV_OVERRIDE_PREFIX.length());

			String decodedKey = EnvVariableDecoder.decode(envKey.toLowerCase());

			model.addAttribute("liferayKey", key);
			model.addAttribute("decodedKey", decodedKey);

			Map<String, Object> keyRow = fetchKeyValue(DECODES_PATH, key);

			Integer hits = (Integer) keyRow.get("hits");

			if (hits == null) {
				hits = 1;
			}
			else {
				hits++;
			}

			keyRow.put("hits", hits);

			saveKeys(DECODES_PATH, key, keyRow);
		}

        return "decode";
    }

	@RequestMapping(value = "/encode/{key}", method = RequestMethod.GET)
	public String encode(Model model, @PathVariable("key") String key) {
		if (key == null || key.isEmpty()) {

			model.addAttribute("error", "Please add a Liferay Portal property");
		}
		else {
			String encodedKey = EnvVariableEncoder.encode(key);

			model.addAttribute("liferayKey", key);
			model.addAttribute("encodedKey", encodedKey);

			Map<String, Object> keyRow = fetchKeyValue(ENCODES_PATH, key);

			Integer hits = (Integer) keyRow.get("hits");

			if (hits == null) {
				hits = 1;
			}
			else {
				hits++;
			}

			keyRow.put("hits", hits);

			saveKeys(ENCODES_PATH, key, keyRow);
		}

		return "encode";
	}


	@RequestMapping("/")
	public ModelAndView index() {
		return new ModelAndView("layout");
	}

	/**
	 * Fetch keys (encodes or decodes) from the datastore.
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> fetchKeyValue(String path, String key) {
		Response response = WeDeploy
			.url(dbUrl)
			.path(path + "/" + key)
			.get();

		if (response.statusCode() == 404) {
			return new HashMap<>();
		}

		if (!response.succeeded()) {
			throw new RuntimeException(
				"Fetching keys failed: " + response.statusCode() + " "
					+ response.statusMessage());
		}

		Map<String, Object> keys = response.bodyMap(String.class, Object.class);

		if (keys == null) {
			return new HashMap<>();
		}

		return keys;
	}

	/**
	 * Saves the keys (encodes or decodes) to the datastore.
	 */
	private void saveKeys(String path, String key, Map<String, Object> keys) {
		Response response = WeDeploy
			.url(dbUrl)
			.path(path + "/" + key)
			.put(keys);

		if (!response.succeeded()) {
			throw new RuntimeException(
				"Saving keys failed: " + response.statusCode() + " " +
					response.statusMessage());
		}
	}

	private static final String DECODES_PATH = "/decodes";
	private static final String ENCODES_PATH = "/encodes";

	private String dbUrl;

	static {
		ApiClient.init();
	}

}